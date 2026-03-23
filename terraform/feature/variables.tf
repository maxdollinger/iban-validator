variable "aws_region" {
  description = "AWS region (must match staging)"
  type        = string
  default     = "eu-central-1"
}

variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "iban"
}

variable "branch_name" {
  description = "Feature branch name (used for resource naming)"
  type        = string
}

variable "hosted_zone_name" {
  description = "Route53 hosted zone name for DNS validation (e.g. example.com)"
  type        = string
}
