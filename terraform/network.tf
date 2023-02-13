resource "aws_vpc" "goals_vpc" {
    cidr_block = "172.17.0.0/16"
    enable_dns_support   = true
    enable_dns_hostnames = true
}


resource "aws_internet_gateway" "internet_gateway" {
    vpc_id = aws_vpc.goals_vpc.id
}

resource "aws_subnet" "pub_subnet" {
    vpc_id                  = aws_vpc.goals_vpc.id
    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 1)
}

resource "aws_route_table" "public" {
    vpc_id = aws_vpc.goals_vpc.id

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.internet_gateway.id
    }
}

resource "aws_route_table_association" "route_table_association" {
    subnet_id      = aws_subnet.pub_subnet.id
    route_table_id = aws_route_table.public.id
}

resource "aws_launch_configuration" "ecs_launch_config" {
    image_id             = "ami-08cd358d745620807"
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