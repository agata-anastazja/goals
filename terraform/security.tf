resource "aws_security_group" "ecs_sg" {
    vpc_id      = aws_vpc.goals_vpc.id
    
    ingress {
        from_port       = 8080
        to_port         = 8080
        protocol        = "tcp"
        security_groups = [aws_security_group.lb.id]
    }

  egress {
    protocol  = "-1"
    from_port = 0
    to_port   = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds_sg" {
    vpc_id      = aws_vpc.goals_vpc.id
    
    ingress {
        from_port       = 5432
        to_port         = 5432
        protocol        = "tcp"
        cidr_blocks     = [aws_vpc.goals_vpc.cidr_block]
    }

    egress {
    protocol  = "-1"
    from_port = 0
    to_port   = 0
    cidr_blocks = ["0.0.0.0/0"]
    }
}

resource "aws_security_group" "lb" {
  name        = "goals-load-balancer-security-group"
  description = "controls access to the ALB"
  vpc_id      = aws_vpc.goals_vpc.id

  ingress {
    protocol  = "tcp"
    from_port = "8080"
    to_port   = "8080"
    cidr_blocks = [
    "0.0.0.0/0"]
  }

  egress {
    # all the protocols
    protocol  = "-1"
    from_port = 0
    to_port   = 0
    cidr_blocks = [
    "0.0.0.0/0"]
  }
}