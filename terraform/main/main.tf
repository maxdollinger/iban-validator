terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
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

  project_name         = var.project_name
  environment          = var.environment
  vpc_cidr             = var.vpc_cidr
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
}

module "database" {
  source = "../modules/database"

  project_name            = var.project_name
  environment             = var.environment
  vpc_id                  = module.networking.vpc_id
  vpc_cidr                = module.networking.vpc_cidr_block
  private_subnet_ids      = module.networking.private_subnet_ids
  db_name                 = var.db_name
  db_username             = var.db_username
  db_engine_version       = var.db_engine_version
  db_instance_class       = var.db_instance_class
  multi_az                = var.rds_multi_az
  skip_final_snapshot     = var.rds_skip_final_snapshot
  backup_retention_period = var.rds_backup_retention_period
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

module "compute" {
  source = "../modules/compute"

  project_name          = var.project_name
  environment           = var.environment
  aws_region            = var.aws_region
  vpc_id                = module.networking.vpc_id
  private_subnet_ids    = module.networking.private_subnet_ids
  alb_security_group_id = module.loadbalancer.alb_security_group_id
  target_group_arn      = module.loadbalancer.target_group_arn
  ecr_repository_url    = aws_ecr_repository.backend.repository_url
  image_tag             = var.environment
  db_endpoint           = module.database.rds_endpoint
  db_name               = var.db_name
  db_secret_arn         = module.database.db_secret_arn
  fargate_cpu           = var.fargate_cpu
  fargate_memory        = var.fargate_memory
  desired_count         = var.backend_desired_count
  ddl_auto              = "update"
}

module "frontend" {
  source = "../modules/frontend"

  project_name = var.project_name
  environment  = var.environment
  alb_dns_name = module.loadbalancer.alb_dns_name
  account_id   = data.aws_caller_identity.current.account_id
}
