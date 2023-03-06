resource "aws_vpc" "goals_vpc" {
    cidr_block = "172.17.0.0/16"
    enable_dns_support   = true
    enable_dns_hostnames = true
}


resource "aws_internet_gateway" "internet_gateway" {
    vpc_id = aws_vpc.goals_vpc.id
}

resource "aws_subnet" "pub_subnet" {
    vpc_id            = aws_vpc.goals_vpc.id
    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 1)
}

data "aws_availability_zones" "available" {}

resource "aws_subnet" "private_subnet_1" {
    vpc_id            = aws_vpc.goals_vpc.id
    availability_zone = data.aws_availability_zones.available.names.0
    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 2)
}

resource "aws_subnet" "private_subnet_2" {
    vpc_id            = aws_vpc.goals_vpc.id
    availability_zone = data.aws_availability_zones.available.names.1
    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 3)
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
