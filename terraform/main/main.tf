terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "iban-terraform-state"
    region         = "eu-central-1"
    dynamodb_table = "iban-terraform-lock"
    encrypt        = true
    # key passed via: tofu init -backend-config="key=prod/terraform.tfstate"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "opentofu"
    }
  }
}

data "aws_caller_identity" "current" {}

# ECR is shared across environments — images are tagged per env/branch
resource "aws_ecr_repository" "backend" {
  name                 = "${var.project_name}-backend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${var.project_name}-backend"
  }
}

module "networking" {
  source = "../modules/networking"

  project_name        = var.project_name
  environment         = var.environment
  vpc_cidr            = var.vpc_cidr
  public_subnet_cidrs = var.public_subnet_cidrs
}

module "certificate" {
  source = "../modules/certificate"

  domain_name      = var.domain_name
  hosted_zone_name = var.hosted_zone_name
}

module "loadbalancer" {
  source = "../modules/loadbalancer"

  project_name      = var.project_name
  environment       = var.environment
  vpc_id            = module.networking.vpc_id
  public_subnet_ids = module.networking.public_subnet_ids
  certificate_arn   = module.certificate.certificate_arn
}

# EFS for persistent SQLite storage
resource "aws_efs_file_system" "data" {
  encrypted = true

  tags = {
    Name = "${var.project_name}-${var.environment}-data"
  }
}

resource "aws_security_group" "efs" {
  name        = "${var.project_name}-${var.environment}-efs-sg"
  description = "Security group for EFS mount targets"
  vpc_id      = module.networking.vpc_id

  tags = {
    Name = "${var.project_name}-${var.environment}-efs-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "efs_from_fargate" {
  security_group_id            = aws_security_group.efs.id
  referenced_security_group_id = module.compute.fargate_security_group_id
  from_port                    = 2049
  to_port                      = 2049
  ip_protocol                  = "tcp"
}

resource "aws_efs_mount_target" "data" {
  count = length(module.networking.public_subnet_ids)

  file_system_id  = aws_efs_file_system.data.id
  subnet_id       = module.networking.public_subnet_ids[count.index]
  security_groups = [aws_security_group.efs.id]
}

# S3 bucket for Litestream SQLite backups
resource "aws_s3_bucket" "litestream_backups" {
  bucket = "${var.project_name}-litestream-${var.environment}-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "${var.project_name}-${var.environment}-litestream-backups"
  }
}

resource "aws_s3_bucket_versioning" "litestream_backups" {
  bucket = aws_s3_bucket.litestream_backups.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "litestream_backups" {
  bucket = aws_s3_bucket.litestream_backups.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
    bucket_key_enabled = true
  }
}

resource "aws_s3_bucket_public_access_block" "litestream_backups" {
  bucket = aws_s3_bucket.litestream_backups.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_lifecycle_configuration" "litestream_backups" {
  bucket = aws_s3_bucket.litestream_backups.id

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"

    noncurrent_version_expiration {
      noncurrent_days = 30
    }
  }
}

module "compute" {
  source = "../modules/compute"

  project_name          = var.project_name
  environment           = var.environment
  aws_region            = var.aws_region
  vpc_id                = module.networking.vpc_id
  subnet_ids            = module.networking.public_subnet_ids
  alb_security_group_id = module.loadbalancer.alb_security_group_id
  target_group_arn      = module.loadbalancer.target_group_arn
  ecr_repository_url    = aws_ecr_repository.backend.repository_url
  image_tag             = var.environment
  fargate_cpu           = var.fargate_cpu
  fargate_memory        = var.fargate_memory
  desired_count         = 1
  use_spot              = var.environment != "prod"
  efs_file_system_id         = aws_efs_file_system.data.id
  litestream_s3_bucket       = aws_s3_bucket.litestream_backups.id
  litestream_s3_path         = "${var.environment}/iban-backup"
  enable_litestream_replicate = true
  enable_litestream_restore   = false
}

module "frontend" {
  source = "../modules/frontend"

  project_name = var.project_name
  environment  = var.environment
  alb_dns_name = module.loadbalancer.alb_dns_name
}
