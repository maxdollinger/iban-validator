data "aws_cloudfront_cache_policy" "caching_optimized" {
  name = "Managed-CachingOptimized"
}

data "aws_cloudfront_cache_policy" "caching_disabled" {
  name = "Managed-CachingDisabled"
}

data "aws_cloudfront_origin_request_policy" "all_viewer" {
  name = "Managed-AllViewer"
}

resource "aws_cloudfront_response_headers_policy" "security" {
  name    = "${var.project_name}-${var.environment}-security-headers"
  comment = "Security headers for ${var.project_name} ${var.environment}"

  security_headers_config {
    strict_transport_security {
      access_control_max_age_sec = 31536000
      include_subdomains         = true
      preload                    = true
      override                   = true
    }

    content_type_options {
      override = true
    }

    frame_options {
      frame_option = "DENY"
      override     = true
    }

    xss_protection {
      mode_block = true
      protection = true
      override   = true
    }

    referrer_policy {
      referrer_policy = "strict-origin-when-cross-origin"
      override        = true
    }

    content_security_policy {
      content_security_policy = "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'"
      override                = true
    }
  }
}

resource "aws_cloudfront_distribution" "main" {
  enabled     = true
  price_class = "PriceClass_100"
  comment     = "${var.project_name} ${var.environment} distribution"

  origin {
    domain_name = var.alb_dns_name
    origin_id   = "alb"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  # API requests — no caching, forward everything
  ordered_cache_behavior {
    path_pattern                 = "/api/v1/*"
    target_origin_id             = "alb"
    viewer_protocol_policy       = "redirect-to-https"
    allowed_methods              = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods               = ["GET", "HEAD"]
    cache_policy_id              = data.aws_cloudfront_cache_policy.caching_disabled.id
    origin_request_policy_id     = data.aws_cloudfront_origin_request_policy.all_viewer.id
    response_headers_policy_id   = aws_cloudfront_response_headers_policy.security.id
    compress                     = true
  }

  # Health/actuator — no caching
  ordered_cache_behavior {
    path_pattern                 = "/actuator/*"
    target_origin_id             = "alb"
    viewer_protocol_policy       = "redirect-to-https"
    allowed_methods              = ["GET", "HEAD"]
    cached_methods               = ["GET", "HEAD"]
    cache_policy_id              = data.aws_cloudfront_cache_policy.caching_disabled.id
    compress                     = true
  }

  # Vite hashed static assets — aggressive caching
  ordered_cache_behavior {
    path_pattern                 = "/assets/*"
    target_origin_id             = "alb"
    viewer_protocol_policy       = "redirect-to-https"
    allowed_methods              = ["GET", "HEAD"]
    cached_methods               = ["GET", "HEAD"]
    cache_policy_id              = data.aws_cloudfront_cache_policy.caching_optimized.id
    response_headers_policy_id   = aws_cloudfront_response_headers_policy.security.id
    compress                     = true
  }

  # Default — no caching (HTML must not be stale)
  default_cache_behavior {
    target_origin_id             = "alb"
    viewer_protocol_policy       = "redirect-to-https"
    allowed_methods              = ["GET", "HEAD", "OPTIONS"]
    cached_methods               = ["GET", "HEAD"]
    cache_policy_id              = data.aws_cloudfront_cache_policy.caching_disabled.id
    origin_request_policy_id     = data.aws_cloudfront_origin_request_policy.all_viewer.id
    response_headers_policy_id   = aws_cloudfront_response_headers_policy.security.id
    compress                     = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-cdn"
  }
}
