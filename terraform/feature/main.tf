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
    # key passed via: tofu init -backend-config="key=feature-<branch>/terraform.tfstate"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project_name
      Environment = local.env
      ManagedBy   = "opentofu"
      Branch      = var.branch_name
    }
  }
}

locals {
  staging     = data.terraform_remote_state.staging.outputs
  env         = "feat-${var.branch_name}"
  domain_name = "${var.branch_name}.${var.hosted_zone_name}"
}

module "certificate" {
  source = "../modules/certificate"

  domain_name      = local.domain_name
  hosted_zone_name = var.hosted_zone_name
}

resource "aws_route53_record" "feature" {
  zone_id = module.certificate.hosted_zone_id
  name    = local.domain_name
  type    = "A"

  alias {
    name                   = local.staging.alb_dns_name
    zone_id                = local.staging.alb_zone_id
    evaluate_target_health = true
  }
}

# Target group for this feature branch (on staging VPC)
resource "aws_lb_target_group" "feature" {
  name        = "${var.project_name}-feat-${var.branch_name}-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = local.staging.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    path                = "/actuator/health"
    port                = "traffic-port"
    protocol            = "HTTP"
    healthy_threshold   = 3
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    matcher             = "200"
  }

  deregistration_delay = 30

  tags = {
    Name = "${var.project_name}-${local.env}-backend-tg"
  }
}

# Host-based routing rule on the staging ALB
resource "aws_lb_listener_rule" "feature" {
  listener_arn = local.staging.https_listener_arn

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.feature.arn
  }

  condition {
    host_header {
      values = [local.domain_name]
    }
  }
}

# Attach feature branch certificate to the staging ALB listener
resource "aws_lb_listener_certificate" "feature" {
  listener_arn    = local.staging.https_listener_arn
  certificate_arn = module.certificate.certificate_arn
}

# Ephemeral compute — restores SQLite from staging S3 on startup, no backup
module "compute" {
  source = "../modules/compute"

  project_name          = var.project_name
  environment           = local.env
  aws_region            = var.aws_region
  vpc_id                = local.staging.vpc_id
  subnet_ids            = local.staging.public_subnet_ids
  alb_security_group_id = local.staging.alb_security_group_id
  target_group_arn      = aws_lb_target_group.feature.arn
  ecr_repository_url    = local.staging.ecr_repository_url
  image_tag             = var.branch_name
  fargate_cpu           = 256
  fargate_memory        = 512
  desired_count         = 1
  use_spot              = true

  # Ephemeral: no EFS, restore from staging S3, no replicate
  efs_file_system_id          = null
  litestream_s3_bucket        = local.staging.litestream_s3_bucket
  litestream_s3_path          = "staging/iban-backup"
  enable_litestream_restore   = true
  enable_litestream_replicate = false
}
