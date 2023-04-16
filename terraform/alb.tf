resource "aws_alb" "goals" {
  name    = "goals-load-balancer"
  subnets = [aws_subnet.pub_subnet.id, aws_subnet.pub_subnet_2.id]
  security_groups = [aws_security_group.lb.id]
}

resource "aws_alb_target_group" "goals_app" {
  name        = "goals-target-group"
  port        = "80"
  protocol    = "HTTP"
  vpc_id      = aws_vpc.goals_vpc.id
  target_type = "ip"

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "3"
    path                = "/"
    unhealthy_threshold = "2"
  }
  lifecycle {
    create_before_destroy = true
  }
}

# Redirect all traffic from the ALB to the target group
resource "aws_alb_listener" "front_end" {
  load_balancer_arn = aws_alb.goals.id
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.goals_app.id
    type             = "forward"
  }
}