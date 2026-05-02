-- 烧烤点单系统数据库
CREATE DATABASE IF NOT EXISTS shaokao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shaokao;

-- 分类表
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '菜品分类';

-- 菜品表
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(500) DEFAULT '' COMMENT '图片URL',
    description VARCHAR(500) DEFAULT '' COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1上架 0下架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category_id)
) COMMENT '菜品';

-- 订单表
CREATE TABLE order_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_no VARCHAR(20) NOT NULL COMMENT '桌号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态 0待确认 1已确认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '订单';

-- 订单明细表
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '菜品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    INDEX idx_order (order_id)
) COMMENT '订单明细';

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '账号',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    nickname VARCHAR(50) DEFAULT '' COMMENT '昵称',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色 admin/user',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '管理员';

-- 默认管理员由应用启动时自动创建, 账号: admin 密码: 123456

-- 示例数据
INSERT INTO category (name, sort_order) VALUES
('烤串', 1),
('海鲜', 2),
('主食', 3),
('饮料', 4);

INSERT INTO product (category_id, name, price, description, sort_order) VALUES
(1, '羊肉串', 5.00, '新鲜羊肉，孜然飘香', 1),
(1, '牛肉串', 6.00, '精选牛肉，口感鲜嫩', 2),
(1, '鸡翅', 8.00, '蜜汁烤鸡翅', 3),
(1, '五花肉', 5.00, '肥瘦相间，入口即化', 4),
(1, '烤腰子', 10.00, '补肾佳品', 5),
(2, '烤生蚝', 8.00, '蒜蓉烤生蚝', 1),
(2, '烤鱿鱼', 12.00, '铁板鱿鱼须', 2),
(2, '烤大虾', 15.00, '新鲜大虾', 3),
(3, '烤馒头片', 3.00, '外焦里嫩', 1),
(3, '烤韭菜', 5.00, '鲜嫩可口', 2),
(3, '烤茄子', 8.00, '蒜蓉烤茄子', 3),
(4, '啤酒', 8.00, '冰镇青岛啤酒', 1),
(4, '王老吉', 5.00, '怕上火喝王老吉', 2),
(4, '酸梅汤', 6.00, '自制酸梅汤', 3);
