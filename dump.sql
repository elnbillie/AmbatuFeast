--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 16.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: my_schema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA my_schema;


ALTER SCHEMA my_schema OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: User; Type: TABLE; Schema: my_schema; Owner: postgres
--

CREATE TABLE my_schema."User" (
    fbid integer NOT NULL,
    userphone character varying(255),
    name character varying(255),
    address text
);


ALTER TABLE my_schema."User" OWNER TO postgres;

--
-- Name: User_fbid_seq; Type: SEQUENCE; Schema: my_schema; Owner: postgres
--

CREATE SEQUENCE my_schema."User_fbid_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE my_schema."User_fbid_seq" OWNER TO postgres;

--
-- Name: User_fbid_seq; Type: SEQUENCE OWNED BY; Schema: my_schema; Owner: postgres
--

ALTER SEQUENCE my_schema."User_fbid_seq" OWNED BY my_schema."User".fbid;


--
-- Name: Favorite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Favorite" (
    email character varying NOT NULL,
    foodid integer,
    restaurantid integer,
    restaurantname character varying(255),
    foodname character varying(255),
    foodimage character varying(255),
    price numeric
);


ALTER TABLE public."Favorite" OWNER TO postgres;

--
-- Name: Favorite_fbid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Favorite_fbid_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Favorite_fbid_seq" OWNER TO postgres;

--
-- Name: Favorite_fbid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Favorite_fbid_seq" OWNED BY public."Favorite".email;


--
-- Name: Order; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Order" (
    orderid integer NOT NULL,
    email character varying(255),
    ordername character varying(255),
    orderaddress text,
    orderstatus integer,
    orderdate date,
    restaurantid integer,
    transactionid character varying(255),
    cod boolean,
    totalprice numeric(10,2),
    numofitem integer
);


ALTER TABLE public."Order" OWNER TO postgres;

--
-- Name: OrderDetail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."OrderDetail" (
    orderid integer,
    itemid integer,
    quantity integer,
    price numeric(10,2),
    discount numeric(10,2),
    size character varying(50),
    addon character varying(50),
    extraprice numeric(10,2)
);


ALTER TABLE public."OrderDetail" OWNER TO postgres;

--
-- Name: Order_orderid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Order_orderid_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Order_orderid_seq" OWNER TO postgres;

--
-- Name: Order_orderid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Order_orderid_seq" OWNED BY public."Order".orderid;


--
-- Name: User; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."User" (
    email character varying(255) NOT NULL,
    userphone character varying(255),
    name character varying(255),
    address text
);


ALTER TABLE public."User" OWNER TO postgres;

--
-- Name: User_fbid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."User_fbid_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."User_fbid_seq" OWNER TO postgres;

--
-- Name: User_fbid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."User_fbid_seq" OWNED BY public."User".email;


--
-- Name: addon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.addon (
    id integer NOT NULL,
    description text,
    extraprice integer
);


ALTER TABLE public.addon OWNER TO postgres;

--
-- Name: addon_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.addon_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.addon_id_seq OWNER TO postgres;

--
-- Name: addon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.addon_id_seq OWNED BY public.addon.id;


--
-- Name: food; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.food (
    id integer NOT NULL,
    name character varying(255),
    description text,
    image character varying(255),
    price integer,
    issize boolean,
    isaddon boolean,
    discount integer
);


ALTER TABLE public.food OWNER TO postgres;

--
-- Name: food_addon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.food_addon (
    foodid integer NOT NULL,
    addonid integer NOT NULL
);


ALTER TABLE public.food_addon OWNER TO postgres;

--
-- Name: food_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.food_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.food_id_seq OWNER TO postgres;

--
-- Name: food_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.food_id_seq OWNED BY public.food.id;


--
-- Name: food_size; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.food_size (
    foodid integer NOT NULL,
    sizeid integer NOT NULL
);


ALTER TABLE public.food_size OWNER TO postgres;

--
-- Name: menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.menu (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    image character varying(255)
);


ALTER TABLE public.menu OWNER TO postgres;

--
-- Name: menu_food; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.menu_food (
    menuid integer NOT NULL,
    foodid integer NOT NULL
);


ALTER TABLE public.menu_food OWNER TO postgres;

--
-- Name: menu_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.menu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.menu_id_seq OWNER TO postgres;

--
-- Name: menu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.menu_id_seq OWNED BY public.menu.id;


--
-- Name: pengguna; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pengguna (
    id integer NOT NULL,
    unique_id character varying(50) NOT NULL,
    name character varying(50),
    email character varying(100),
    encrypted_password character varying(128),
    salt character varying(16),
    created_at timestamp without time zone,
    updated_at timestamp without time zone
);


ALTER TABLE public.pengguna OWNER TO postgres;

--
-- Name: pengguna_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pengguna_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pengguna_id_seq OWNER TO postgres;

--
-- Name: pengguna_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pengguna_id_seq OWNED BY public.pengguna.id;


--
-- Name: restaurant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.restaurant (
    id integer NOT NULL,
    name character varying(255),
    address character varying(255),
    phone character varying(20),
    lat double precision,
    lng double precision,
    userowner integer,
    image text,
    paymenturl text
);


ALTER TABLE public.restaurant OWNER TO postgres;

--
-- Name: restaurant_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.restaurant_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.restaurant_id_seq OWNER TO postgres;

--
-- Name: restaurant_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.restaurant_id_seq OWNED BY public.restaurant.id;


--
-- Name: restaurant_menu; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.restaurant_menu (
    restaurantid integer NOT NULL,
    menuid integer NOT NULL
);


ALTER TABLE public.restaurant_menu OWNER TO postgres;

--
-- Name: size; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.size (
    id integer NOT NULL,
    description character varying(255),
    extraprice integer
);


ALTER TABLE public.size OWNER TO postgres;

--
-- Name: size_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.size_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.size_id_seq OWNER TO postgres;

--
-- Name: size_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.size_id_seq OWNED BY public.size.id;


--
-- Name: User fbid; Type: DEFAULT; Schema: my_schema; Owner: postgres
--

ALTER TABLE ONLY my_schema."User" ALTER COLUMN fbid SET DEFAULT nextval('my_schema."User_fbid_seq"'::regclass);


--
-- Name: Order orderid; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Order" ALTER COLUMN orderid SET DEFAULT nextval('public."Order_orderid_seq"'::regclass);


--
-- Name: User email; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User" ALTER COLUMN email SET DEFAULT nextval('public."User_fbid_seq"'::regclass);


--
-- Name: addon id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addon ALTER COLUMN id SET DEFAULT nextval('public.addon_id_seq'::regclass);


--
-- Name: food id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food ALTER COLUMN id SET DEFAULT nextval('public.food_id_seq'::regclass);


--
-- Name: menu id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu ALTER COLUMN id SET DEFAULT nextval('public.menu_id_seq'::regclass);


--
-- Name: pengguna id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pengguna ALTER COLUMN id SET DEFAULT nextval('public.pengguna_id_seq'::regclass);


--
-- Name: restaurant id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.restaurant ALTER COLUMN id SET DEFAULT nextval('public.restaurant_id_seq'::regclass);


--
-- Name: size id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.size ALTER COLUMN id SET DEFAULT nextval('public.size_id_seq'::regclass);


--
-- Name: User User_pkey; Type: CONSTRAINT; Schema: my_schema; Owner: postgres
--

ALTER TABLE ONLY my_schema."User"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (fbid);


--
-- Name: Order Order_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Order"
    ADD CONSTRAINT "Order_pkey" PRIMARY KEY (orderid);


--
-- Name: User User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (email);


--
-- Name: addon addon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.addon
    ADD CONSTRAINT addon_pkey PRIMARY KEY (id);


--
-- Name: food_addon food_addon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_addon
    ADD CONSTRAINT food_addon_pkey PRIMARY KEY (foodid, addonid);


--
-- Name: food food_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food
    ADD CONSTRAINT food_pkey PRIMARY KEY (id);


--
-- Name: food_size food_size_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_size
    ADD CONSTRAINT food_size_pkey PRIMARY KEY (foodid, sizeid);


--
-- Name: menu_food menu_food_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu_food
    ADD CONSTRAINT menu_food_pkey PRIMARY KEY (menuid, foodid);


--
-- Name: menu menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu
    ADD CONSTRAINT menu_pkey PRIMARY KEY (id);


--
-- Name: pengguna pengguna_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pengguna
    ADD CONSTRAINT pengguna_pkey PRIMARY KEY (id);


--
-- Name: pengguna pengguna_unique_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pengguna
    ADD CONSTRAINT pengguna_unique_id_key UNIQUE (unique_id);


--
-- Name: restaurant_menu restaurant_menu_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.restaurant_menu
    ADD CONSTRAINT restaurant_menu_pkey PRIMARY KEY (restaurantid, menuid);


--
-- Name: restaurant restaurant_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.restaurant
    ADD CONSTRAINT restaurant_pkey PRIMARY KEY (id);


--
-- Name: size size_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.size
    ADD CONSTRAINT size_pkey PRIMARY KEY (id);


--
-- Name: Favorite unique_favorite; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Favorite"
    ADD CONSTRAINT unique_favorite UNIQUE (email, foodid, restaurantid);


--
-- Name: food_addon food_addon_addonid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_addon
    ADD CONSTRAINT food_addon_addonid_fkey FOREIGN KEY (addonid) REFERENCES public.addon(id) ON DELETE CASCADE;


--
-- Name: food_addon food_addon_foodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_addon
    ADD CONSTRAINT food_addon_foodid_fkey FOREIGN KEY (foodid) REFERENCES public.food(id) ON DELETE CASCADE;


--
-- Name: food_size food_size_foodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_size
    ADD CONSTRAINT food_size_foodid_fkey FOREIGN KEY (foodid) REFERENCES public.food(id) ON DELETE CASCADE;


--
-- Name: food_size food_size_sizeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.food_size
    ADD CONSTRAINT food_size_sizeid_fkey FOREIGN KEY (sizeid) REFERENCES public.size(id) ON DELETE CASCADE;


--
-- Name: menu_food menu_food_foodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu_food
    ADD CONSTRAINT menu_food_foodid_fkey FOREIGN KEY (foodid) REFERENCES public.food(id) ON DELETE CASCADE;


--
-- Name: menu_food menu_food_menuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.menu_food
    ADD CONSTRAINT menu_food_menuid_fkey FOREIGN KEY (menuid) REFERENCES public.menu(id) ON DELETE CASCADE;


--
-- Name: restaurant_menu restaurant_menu_menuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.restaurant_menu
    ADD CONSTRAINT restaurant_menu_menuid_fkey FOREIGN KEY (menuid) REFERENCES public.menu(id) ON DELETE CASCADE;


--
-- Name: restaurant_menu restaurant_menu_restaurantid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.restaurant_menu
    ADD CONSTRAINT restaurant_menu_restaurantid_fkey FOREIGN KEY (restaurantid) REFERENCES public.restaurant(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

