resource "aws_cloudwatch_log_group" "goals_log_group" {
  name              = "/ecs/goals-app"
  retention_in_days = 30

  tags = {
    Name = "goals-log-group"
  }
}

resource "aws_cloudwatch_log_stream" "goals_log_stream" {
  name           = "goals-log-stream"
  log_group_name = aws_cloudwatch_log_group.goals_log_group.name
}