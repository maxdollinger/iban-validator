output "alb_dns_name" {
  description = "ALB DNS name for this feature branch"
  value       = module.loadbalancer.alb_dns_name
}

output "feature_url" {
  description = "HTTPS URL for this feature branch"
  value       = "https://${local.domain_name}"
}
