resource "aws_vpc" "goals_vpc" {
    cidr_block = "172.17.0.0/16"
    enable_dns_support   = true
    enable_dns_hostnames = true
}


resource "aws_internet_gateway" "internet_gateway" {
    vpc_id = aws_vpc.goals_vpc.id
}

data "aws_availability_zones" "available" {}


resource "aws_subnet" "pub_subnet" {
    vpc_id            = aws_vpc.goals_vpc.id
    availability_zone = data.aws_availability_zones.available.names.0

    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 1)
}

resource "aws_subnet" "pub_subnet_2" {
    vpc_id            = aws_vpc.goals_vpc.id
    availability_zone = data.aws_availability_zones.available.names.1

    cidr_block        = cidrsubnet(aws_vpc.goals_vpc.cidr_block, 8, 4)
}


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

resource "aws_route" "internet_access" {
  route_table_id         = aws_vpc.goals_vpc.main_route_table_id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.internet_gateway.id
}

# Create a NAT gateway with an Elastic IP for each private subnet to get internet connectivity
resource "aws_eip" "gw" {
  count = 2
  vpc   = true
  depends_on = [
  aws_internet_gateway.internet_gateway]
}

resource "aws_nat_gateway" "gw_1" {
  subnet_id     = aws_subnet.pub_subnet.id
  allocation_id = element(aws_eip.gw.*.id, 0)
}

resource "aws_nat_gateway" "gw_2" {
  subnet_id     = aws_subnet.pub_subnet_2.id
  allocation_id = element(aws_eip.gw.*.id, 1)
}
# Create a new route table for the private subnets, make it route non-local traffic through the NAT gateway to the internet
resource "aws_route_table" "private_1" {
  vpc_id = aws_vpc.goals_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.gw_1.id
  }
}

resource "aws_route_table" "private_2" {
  vpc_id = aws_vpc.goals_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.gw_2.id
  }
}

# Explicitly associate the newly created route tables to the private subnets (so they don't default to the main route table)
resource "aws_route_table_association" "private_1" {
  subnet_id      = aws_subnet.private_subnet_1.id
  route_table_id = aws_route_table.private_1.id
}

resource "aws_route_table_association" "private_2" {
  subnet_id      = aws_subnet.private_subnet_1.id
  route_table_id = aws_route_table.private_1.id
}

resource "aws_route_table_association" "private_3" {
  subnet_id      = aws_subnet.private_subnet_2.id
  route_table_id = aws_route_table.private_2.id 
}

resource "aws_route_table_association" "private_4" {
  subnet_id      = aws_subnet.private_subnet_2.id
  route_table_id = aws_route_table.private_2.id  
}