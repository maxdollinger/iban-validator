output "rds_endpoint" {
  value = aws_db_instance.main.endpoint
}

output "rds_address" {
  value = aws_db_instance.main.address
}

output "rds_port" {
  value = aws_db_instance.main.port
}

output "db_secret_arn" {
  value = aws_secretsmanager_secret.db.arn
}

output "db_master_password" {
  value     = random_password.db.result
  sensitive = true
}

output "rds_security_group_id" {
  value = aws_security_group.rds.id
}
