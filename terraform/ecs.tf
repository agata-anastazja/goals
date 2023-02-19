

resource "aws_launch_configuration" "ecs_launch_config" {
    image_id             = "ami-07e394e4df20de8d2"
    iam_instance_profile = aws_iam_instance_profile.ecs_agent.name
    security_groups      = [aws_security_group.ecs_sg.id]
    user_data            = "#!/bin/bash\necho ECS_CLUSTER=my-cluster >> /etc/ecs/ecs.config"
    instance_type        = "t2.micro"
}

resource "aws_autoscaling_group" "goals_ecs_asg" {
    name                      = "asg"
    vpc_zone_identifier       = [aws_subnet.pub_subnet.id]
    launch_configuration      = aws_launch_configuration.ecs_launch_config.name

    desired_capacity          = 1
    min_size                  = 1
    max_size                  = 1
    health_check_grace_period = 300
    health_check_type         = "EC2"
}

resource "aws_ecs_capacity_provider" "goals_ecs" {
  name = "goals_ecs"

  auto_scaling_group_provider {
    auto_scaling_group_arn         = aws_autoscaling_group.goals_ecs_asg.arn

    managed_scaling {
      maximum_scaling_step_size = 1
      minimum_scaling_step_size = 1
      status                    = "ENABLED"
      target_capacity           = 5
    }
  }
}

resource "aws_ecs_cluster" "ecs_cluster" {
    name  = "goals-cluster"
}

resource "aws_ecs_task_definition" "service" {
  family = "goals-service"
  container_definitions = jsonencode([
    {
      name      = "goals"
      image     = "agatasumowska/goals"
      cpu       = 1024
      memory    = 1024
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 80
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
}