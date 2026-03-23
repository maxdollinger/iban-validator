variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "aws_region" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

variable "alb_security_group_id" {
  type = string
}

variable "target_group_arn" {
  type = string
}

variable "ecr_repository_url" {
  type = string
}

variable "image_tag" {
  type    = string
  default = "latest"
}

variable "fargate_cpu" {
  type = number
}

variable "fargate_memory" {
  type = number
}

variable "desired_count" {
  type    = number
  default = 1

  validation {
    condition     = var.desired_count == 1
    error_message = "SQLite supports only a single writer. desired_count must be 1."
  }
}

variable "use_spot" {
  description = "Use Fargate Spot capacity provider for cost savings"
  type        = bool
  default     = false
}

variable "efs_file_system_id" {
  description = "EFS file system ID for persistent storage. Null uses ephemeral storage."
  type        = string
  default     = null
}

variable "litestream_s3_bucket" {
  description = "S3 bucket for Litestream backup/restore"
  type        = string
}

variable "litestream_s3_path" {
  description = "S3 path prefix within the bucket"
  type        = string
}

variable "enable_litestream_replicate" {
  description = "Run Litestream sidecar for continuous S3 backup"
  type        = bool
  default     = true
}

variable "enable_litestream_restore" {
  description = "Restore SQLite from S3 on startup (for ephemeral environments)"
  type        = bool
  default     = false
}

variable "sqlite_db_path" {
  type    = string
  default = "/data/iban.db"
}

variable "litestream_image" {
  type    = string
  default = "litestream/litestream:latest"
}
