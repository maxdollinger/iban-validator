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

output "litestream_s3_bucket" {
  description = "S3 bucket for Litestream SQLite backups"
  value       = aws_s3_bucket.litestream_backups.id
}

output "efs_file_system_id" {
  description = "EFS file system ID for SQLite storage"
  value       = aws_efs_file_system.data.id
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

output "alb_zone_id" {
  description = "ALB hosted zone ID for feature branch DNS"
  value       = module.loadbalancer.alb_zone_id
}

output "alb_security_group_id" {
  description = "ALB security group ID for feature branch compute"
  value       = module.loadbalancer.alb_security_group_id
}

output "https_listener_arn" {
  description = "HTTPS listener ARN for feature branch routing rules"
  value       = module.loadbalancer.https_listener_arn
}
