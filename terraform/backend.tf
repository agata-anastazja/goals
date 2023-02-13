terraform {
    backend "s3" {
        bucket = "agata-tf-states"
        key    = "goals.tfstate"
        region = "eu-west-2"
    }
}