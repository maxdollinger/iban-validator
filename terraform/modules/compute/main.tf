resource "aws_security_group" "fargate" {
  name        = "${var.project_name}-${var.environment}-fargate-sg"
  description = "Security group for Fargate tasks"
  vpc_id      = var.vpc_id

  tags = {
    Name = "${var.project_name}-${var.environment}-fargate-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "fargate_from_alb" {
  security_group_id            = aws_security_group.fargate.id
  referenced_security_group_id = var.alb_security_group_id
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "fargate_all" {
  security_group_id = aws_security_group.fargate.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}

resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.environment}"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-cluster"
  }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = var.use_spot ? ["FARGATE", "FARGATE_SPOT"] : ["FARGATE"]
}

resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/${var.project_name}-${var.environment}-backend"
  retention_in_days = 30

  tags = {
    Name = "${var.project_name}-${var.environment}-backend-logs"
  }
}

resource "aws_cloudwatch_log_group" "litestream" {
  count             = var.enable_litestream_replicate || var.enable_litestream_restore ? 1 : 0
  name              = "/ecs/${var.project_name}-${var.environment}-litestream"
  retention_in_days = 14

  tags = {
    Name = "${var.project_name}-${var.environment}-litestream-logs"
  }
}

resource "aws_iam_role" "ecs_task_execution" {
  name = "${var.project_name}-${var.environment}-ecs-task-execution"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-${var.environment}-ecs-task-execution"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task" {
  name = "${var.project_name}-${var.environment}-ecs-task"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-${var.environment}-ecs-task"
  }
}

resource "aws_iam_role_policy" "litestream_s3" {
  name = "${var.project_name}-${var.environment}-litestream-s3"
  role = aws_iam_role.ecs_task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::${var.litestream_s3_bucket}",
          "arn:aws:s3:::${var.litestream_s3_bucket}/*"
        ]
      }
    ]
  })
}

locals {
  litestream_log_group = var.enable_litestream_replicate || var.enable_litestream_restore ? aws_cloudwatch_log_group.litestream[0].name : ""

  restore_container = var.enable_litestream_restore ? [
    {
      name      = "litestream-restore"
      image     = var.litestream_image
      essential = false

      command = ["restore", "-if-replica-exists", var.sqlite_db_path]

      environment = [
        { name = "LITESTREAM_S3_BUCKET", value = var.litestream_s3_bucket },
        { name = "LITESTREAM_S3_PATH", value = var.litestream_s3_path },
        { name = "AWS_REGION", value = var.aws_region }
      ]

      mountPoints = [
        {
          sourceVolume  = "data"
          containerPath = "/data"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = local.litestream_log_group
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "restore"
        }
      }
    }
  ] : []

  backend_container = [
    {
      name      = "backend"
      image     = "${var.ecr_repository_url}:${var.image_tag}"
      essential = true

      dependsOn = var.enable_litestream_restore ? [
        {
          containerName = "litestream-restore"
          condition     = "SUCCESS"
        }
      ] : []

      portMappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SQLITE_DB_PATH"
          value = var.sqlite_db_path
        },
        {
          name  = "SPRING_JPA_HIBERNATE_DDL_AUTO"
          value = "update"
        }
      ]

      mountPoints = [
        {
          sourceVolume  = "data"
          containerPath = "/data"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.backend.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ]

  replicate_container = var.enable_litestream_replicate ? [
    {
      name      = "litestream-replicate"
      image     = var.litestream_image
      essential = true

      dependsOn = [
        {
          containerName = "backend"
          condition     = "START"
        }
      ]

      command = [
        "replicate",
        var.sqlite_db_path,
        "s3://${var.litestream_s3_bucket}/${var.litestream_s3_path}"
      ]

      environment = [
        {
          name  = "AWS_REGION"
          value = var.aws_region
        }
      ]

      mountPoints = [
        {
          sourceVolume  = "data"
          containerPath = "/data"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = local.litestream_log_group
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "replicate"
        }
      }
    }
  ] : []
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "${var.project_name}-${var.environment}-backend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn            = aws_iam_role.ecs_task.arn

  volume {
    name = "data"

    dynamic "efs_volume_configuration" {
      for_each = var.efs_file_system_id != null ? [1] : []
      content {
        file_system_id = var.efs_file_system_id
        root_directory = "/"
      }
    }
  }

  container_definitions = jsonencode(
    concat(local.restore_container, local.backend_container, local.replicate_container)
  )

  tags = {
    Name = "${var.project_name}-${var.environment}-backend-task"
  }
}

resource "aws_ecs_service" "backend" {
  name            = "${var.project_name}-${var.environment}-backend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.desired_count

  dynamic "capacity_provider_strategy" {
    for_each = var.use_spot ? [1] : []
    content {
      capacity_provider = "FARGATE_SPOT"
      weight            = 1
      base              = 0
    }
  }

  dynamic "capacity_provider_strategy" {
    for_each = var.use_spot ? [] : [1]
    content {
      capacity_provider = "FARGATE"
      weight            = 1
    }
  }

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = [aws_security_group.fargate.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "backend"
    container_port   = 8080
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-backend-service"
  }
}
