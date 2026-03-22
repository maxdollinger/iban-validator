output "cloudfront_distribution_domain" {
  description = "CloudFront distribution domain name (app URL)"
  value       = module.frontend.cloudfront_domain
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID (for cache invalidation)"
  value       = module.frontend.cloudfront_distribution_id
}

output "alb_dns_name" {
  description = "ALB DNS name"
  value       = module.loadbalancer.alb_dns_name
}

output "ecr_repository_url" {
  description = "ECR repository URL for backend image"
  value       = aws_ecr_repository.backend.repository_url
}

output "s3_bucket_name" {
  description = "S3 bucket name for frontend deployment"
  value       = module.frontend.s3_bucket_name
}

output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = module.database.rds_endpoint
  sensitive   = true
}

# Outputs consumed by feature branch deployments via terraform_remote_state
output "vpc_id" {
  description = "VPC ID for feature branches"
  value       = module.networking.vpc_id
}

output "public_subnet_ids" {
  description = "Public subnet IDs for feature branches"
  value       = module.networking.public_subnet_ids
}

output "private_subnet_ids" {
  description = "Private subnet IDs for feature branches"
  value       = module.networking.private_subnet_ids
}

output "rds_address" {
  description = "RDS hostname for feature branches"
  value       = module.database.rds_address
  sensitive   = true
}

output "rds_port" {
  description = "RDS port for feature branches"
  value       = module.database.rds_port
}

output "db_secret_arn" {
  description = "Secrets Manager ARN for DB credentials"
  value       = module.database.db_secret_arn
}
