# Creates a database on the shared staging RDS instance.
# Requires network access to the RDS (run from CI runner in the staging VPC,
# or via SSH tunnel / SSM session to the private subnet).
provider "postgresql" {
  host     = local.staging.rds_address
  port     = local.staging.rds_port
  username = var.db_master_username
  password = var.db_master_password
  sslmode  = "require"
}

resource "postgresql_database" "feature" {
  name = local.db_name
}
