resource "aws_ecs_cluster" "ecs_cluster" {
    name  = "goals-cluster"
}

resource "aws_ecs_task_definition" "service" {
  family = "goals-service"
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  network_mode       = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                   = 512
  memory                = 1024
  
  container_definitions = jsonencode([
    {
      name      = "goals-app"
      image     = "agatasumowska/goals"
      cpu       = 512
      memory    = 1024
      essential = true
      environment = [{"name": "DB_JDBC_URI", "value": "jdbc:postgresql://${aws_db_instance.goals.endpoint}/goals?user=goals&password=goalsgoals" }]
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
    }]
  )
}

resource "aws_ecs_service" "worker" {
  name            = "worker"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.service.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    security_groups = [aws_security_group.ecs_sg.id]
    subnets          = [aws_subnet.private_subnet_1.id, aws_subnet.private_subnet_2.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.goals_app.id
    container_name   = "goals-app"
    container_port   = "8080"
  }

  depends_on = [
    aws_alb_listener.front_end,
    aws_iam_role_policy_attachment.ecs_task_execution_role]
}