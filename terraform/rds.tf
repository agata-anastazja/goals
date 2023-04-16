resource "aws_db_instance" "goals" {
  identifier             = "goals"
  instance_class         = "db.t4g.micro"
  allocated_storage      = 5
  engine                 = "postgres"
  engine_version         = "14.6"
  db_name                = "goals"
  username               = "goals"
  password               = "goalsgoals"
  db_subnet_group_name   = aws_db_subnet_group.goals.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  parameter_group_name   = aws_db_parameter_group.goals.name
  skip_final_snapshot    = true
}

resource "aws_db_subnet_group" "goals" {
  name       = "goals"
  subnet_ids = [aws_subnet.private_subnet_1.id, aws_subnet.private_subnet_2.id]
}

resource "aws_db_parameter_group" "goals" {
  name   = "goals"
  family = "postgres14"

  parameter {
    name  = "log_connections"
    value = "1"
  }
}