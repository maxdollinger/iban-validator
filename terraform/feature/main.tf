terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "~> 1.22"
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

data "aws_caller_identity" "current" {}

locals {
  staging     = data.terraform_remote_state.staging.outputs
  env         = "feat-${var.branch_name}"
  db_name     = "iban_${replace(var.branch_name, "-", "_")}"
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
    name                   = module.loadbalancer.alb_dns_name
    zone_id                = module.loadbalancer.alb_zone_id
    evaluate_target_health = true
  }
}

module "loadbalancer" {
  source = "../modules/loadbalancer"

  project_name      = var.project_name
  environment       = local.env
  vpc_id            = local.staging.vpc_id
  public_subnet_ids = local.staging.public_subnet_ids
  certificate_arn   = module.certificate.certificate_arn
}

module "compute" {
  source = "../modules/compute"

  project_name          = var.project_name
  environment           = local.env
  aws_region            = var.aws_region
  vpc_id                = local.staging.vpc_id
  private_subnet_ids    = local.staging.private_subnet_ids
  alb_security_group_id = module.loadbalancer.alb_security_group_id
  target_group_arn      = module.loadbalancer.target_group_arn
  ecr_repository_url    = local.staging.ecr_repository_url
  image_tag             = var.branch_name
  db_endpoint           = local.staging.rds_endpoint
  db_name               = local.db_name
  db_secret_arn         = local.staging.db_secret_arn
  fargate_cpu           = 256
  fargate_memory        = 512
  desired_count         = 1
  ddl_auto              = "update"
}

module "frontend" {
  source = "../modules/frontend"

  project_name = var.project_name
  environment  = local.env
  alb_dns_name = module.loadbalancer.alb_dns_name
  account_id   = data.aws_caller_identity.current.account_id
}
