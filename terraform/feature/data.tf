data "terraform_remote_state" "staging" {
  backend = "s3"

  config = {
    bucket = "iban-terraform-state"
    key    = "staging/terraform.tfstate"
    region = "eu-central-1"
  }
}
