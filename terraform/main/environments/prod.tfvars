environment                 = "prod"
db_instance_class           = "db.t4g.small"
fargate_cpu                 = 1024
fargate_memory              = 2048
backend_desired_count       = 2
rds_multi_az                = true
rds_skip_final_snapshot     = false
rds_backup_retention_period = 7
