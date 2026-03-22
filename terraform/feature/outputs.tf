output "cloudfront_distribution_domain" {
  description = "CloudFront domain for this feature branch"
  value       = module.frontend.cloudfront_domain
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID (for cache invalidation)"
  value       = module.frontend.cloudfront_distribution_id
}

output "alb_dns_name" {
  description = "ALB DNS name for this feature branch"
  value       = module.loadbalancer.alb_dns_name
}

output "s3_bucket_name" {
  description = "S3 bucket for frontend deployment"
  value       = module.frontend.s3_bucket_name
}

output "feature_db_name" {
  description = "Database name created for this feature branch"
  value       = local.db_name
}
