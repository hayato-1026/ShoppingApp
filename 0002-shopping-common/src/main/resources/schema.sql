create table if not exists t_department (
    code integer primary key,
    name varchar(100),
    discount_rate decimal(5,4),
    tax_rate decimal(5,4),
    department_sales decimal(15,2)
);

create table if not exists t_product  (
    id varchar(100) primary key,
    name varchar(100),
    price integer,
    stock integer,
    department_code integer,
    tax_rate decimal(5,4),
    discount_rate decimal(5,4),
    point_mag decimal(6,2) default 0,
    foreign key (department_code) references t_department(code)
);

create table if not exists t_order (
    id varchar(100) primary key,
    order_date_time timestamp,
    billing_amount integer,
    customer_name varchar(100),
    customer_address varchar(100),
    customer_phone varchar(100),
    customer_email_address varchar(100),
    payment_method varchar(100),
    points_used decimal(15,2) default 0,
    points_earned decimal(15,2) default 0
);

create table if not exists t_order_item (
    id varchar(100) primary key,
    order_id varchar(100),
    product_id varchar(100),
    price_at_order integer,
    quantity integer,
    foreign key (order_id) references t_order(id),
    foreign key (product_id) references t_product(id)
);

create table if not exists t_user (
    id varchar(100) primary key,
    username varchar(100) UNIQUE not null,
    password varchar(255) not null,
    role varchar(50) not null,
    items_purchased integer default 0,
    purchase_count integer default 0,
    total_spent decimal(15,2) default 0,
    points decimal(15,2) default 0,
    point_rate decimal(6,4) default 0
);