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
  description = "Feature branch name (used for resource naming and DB name)"
  type        = string
}

variable "db_master_username" {
  description = "RDS master username for creating the feature database"
  type        = string
  default     = "iban_app"
}

variable "db_master_password" {
  description = "RDS master password for creating the feature database"
  type        = string
  sensitive   = true
}
