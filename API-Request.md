# API 请求示例文档

本文档包含了电商平台系统中所有REST API接口的示例请求体。

## 基础信息
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`

---

## 1. 用户管理 API (/users)

### 1.1 创建用户
**POST** `/users`

```json
{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "currency": "CNY"
}
```

### 1.2 用户充值
**POST** `/users/{userId}/recharge`

```json
{
  "amount": 1000.00,
  "currency": "CNY"
}
```

### 1.3 根据ID获取用户
**GET** `/users/{userId}`

无请求体

### 1.4 根据用户名获取用户
**GET** `/users?username={username}`

无请求体

---

## 2. 商家管理 API (/merchants)

### 2.1 创建商家
**POST** `/merchants`

```json
{
  "name": "阿里巴巴电商店铺",
  "email": "merchant@alibaba.com",
  "phone": "13800138000",
  "currency": "CNY"
}
```

### 2.2 根据ID获取商家
**GET** `/merchants/{merchantId}`

无请求体

### 2.3 根据邮箱获取商家
**GET** `/merchants?email={email}`

无请求体

---

## 3. 商品管理 API (/products)

### 3.1 创建商品
**POST** `/products`

```json
{
  "sku": "PHONE-IPHONE15-128GB",
  "name": "iPhone 15 128GB",
  "description": "苹果iPhone 15，128GB存储，支持5G网络",
  "price": 5999.00,
  "currency": "CNY",
  "stockQuantity": 100,
  "merchantId": 1
}
```

### 3.2 添加库存
**POST** `/products/{sku}/stock`

```json
{
  "quantity": 50
}
```

### 3.3 根据SKU获取商品
**GET** `/products/{sku}`

无请求体

### 3.4 获取所有活跃商品
**GET** `/products`

无请求体

### 3.5 根据商家ID获取商品列表
**GET** `/products/merchant/{merchantId}`

无请求体

---

## 4. 订单管理 API (/orders)

### 4.1 创建订单
**POST** `/orders`

```json
{
  "userId": 1,
  "items": [
    {
      "sku": "PHONE-IPHONE15-128GB",
      "quantity": 2
    },
    {
      "sku": "PHONE-IPHONE15-256GB",
      "quantity": 1
    }
  ]
}
```

### 4.2 根据订单号获取订单
**GET** `/orders/{orderNumber}`

无请求体

### 4.3 根据用户ID获取订单列表
**GET** `/orders/user/{userId}`

无请求体

### 4.4 根据商家ID获取订单列表
**GET** `/orders/merchant/{merchantId}`

无请求体

---

## 5. 完整业务流程示例

### 5.1 完整的电商购买流程

#### 步骤1: 创建用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer001",
    "email": "buyer001@example.com",
    "currency": "CNY"
  }'
```

#### 步骤2: 创建商家
```bash
curl -X POST http://localhost:8080/api/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "科技数码专营店",
    "email": "tech@store.com",
    "phone": "13900139000",
    "currency": "CNY"
  }'
```

#### 步骤3: 创建商品
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAPTOP-MACBOOK-PRO-16",
    "name": "MacBook Pro 16英寸",
    "description": "苹果MacBook Pro 16英寸，M3 Pro芯片，512GB SSD",
    "price": 18999.00,
    "currency": "CNY",
    "stockQuantity": 20,
    "merchantId": 1
  }'
```

#### 步骤4: 用户充值
```bash
curl -X POST http://localhost:8080/api/users/1/recharge \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 25000.00,
    "currency": "CNY"
  }'
```

#### 步骤5: 创建订单
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "sku": "LAPTOP-MACBOOK-PRO-16",
        "quantity": 1
      }
    ]
  }'
```

---

## 6. 错误处理示例

### 6.1 参数验证错误
当请求参数不符合验证规则时，API会返回400状态码：

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users"
}
```

### 6.2 资源不存在错误
当请求的资源不存在时，API会返回404状态码：

```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 999",
  "path": "/api/users/999"
}
```

---

## 7. 注意事项

1. **货币字段**: 所有涉及金额的字段都支持currency参数，默认为"CNY"
2. **ID字段**: 创建资源时不需要提供ID，系统会自动生成
3. **SKU唯一性**: 商品SKU在系统中必须唯一
4. **邮箱唯一性**: 用户和商家的邮箱在各自领域内必须唯一
5. **库存检查**: 创建订单时会自动检查库存是否充足
6. **余额检查**: 创建订单时会自动检查用户余额是否充足
7. **同商家限制**: 一个订单中的所有商品必须属于同一个商家

---

## 8. 系统监控接口

### 8.1 健康检查
**GET** `/actuator/health`

### 8.2 H2数据库控制台
**访问**: `http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:mem:ecommerce`
- Username: `sa`
- Password: (空)