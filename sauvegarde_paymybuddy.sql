--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-07-15 21:36:26

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4962 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16528)
-- Name: connexion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.connexion (
    user_id integer NOT NULL,
    connected_to_user integer NOT NULL
);


ALTER TABLE public.connexion OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16531)
-- Name: transaction; Type: TABLE; Schema: public; Owner: myappuser
--

CREATE TABLE public.transaction (
    id integer NOT NULL,
    description character varying(200),
    amount numeric(15,2) NOT NULL,
    from_user_id integer,
    to_user_id integer
);


ALTER TABLE public.transaction OWNER TO myappuser;

--
-- TOC entry 220 (class 1259 OID 16534)
-- Name: transaction_id_seq; Type: SEQUENCE; Schema: public; Owner: myappuser
--

CREATE SEQUENCE public.transaction_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transaction_id_seq OWNER TO myappuser;

--
-- TOC entry 4964 (class 0 OID 0)
-- Dependencies: 220
-- Name: transaction_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: myappuser
--

ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;


--
-- TOC entry 221 (class 1259 OID 16535)
-- Name: users; Type: TABLE; Schema: public; Owner: myappuser
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    username character varying(100) NOT NULL,
    email character varying(200) NOT NULL,
    password text NOT NULL,
    solde numeric(15,2) DEFAULT 0 NOT NULL,
    role character varying(50) DEFAULT 'user'::character varying NOT NULL
);


ALTER TABLE public.users OWNER TO myappuser;

--
-- TOC entry 222 (class 1259 OID 16541)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: myappuser
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO myappuser;

--
-- TOC entry 4965 (class 0 OID 0)
-- Dependencies: 222
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: myappuser
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4789 (class 2604 OID 16542)
-- Name: transaction id; Type: DEFAULT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.transaction ALTER COLUMN id SET DEFAULT nextval('public.transaction_id_seq'::regclass);


--
-- TOC entry 4790 (class 2604 OID 16543)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4952 (class 0 OID 16528)
-- Dependencies: 218
-- Data for Name: connexion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.connexion (user_id, connected_to_user) FROM stdin;
1	2
1	3
2	3
\.


--
-- TOC entry 4953 (class 0 OID 16531)
-- Dependencies: 219
-- Data for Name: transaction; Type: TABLE DATA; Schema: public; Owner: myappuser
--

COPY public.transaction (id, description, amount, from_user_id, to_user_id) FROM stdin;
1	Achat de livre	26.35	1	2
2	Remboursement dîner	42.50	2	3
3	Partage abonnement	18.00	3	1
4	Dépannage voiture	19.99	2	1
5	Billets de concert	70.00	3	2
\.


--
-- TOC entry 4955 (class 0 OID 16535)
-- Dependencies: 221
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: myappuser
--

COPY public.users (user_id, username, email, password, solde, role) FROM stdin;
1	alice	alice@mail.com	$2a$06$SqUY61I8leTmRy/lhk8kVOw/krMu4QcSTHPJcncJpqySUhIkr2vBS	0.00	user
2	bob	bob@mail.com	$2a$06$s14RrQoxK40v4x.gsZEcROqKDr4WFKQOnYu09GuGThDYYTnCf9RHy	0.00	user
3	carol	carol@mail.com	$2a$06$K4Bxqlhy80w8gWqb6gjCw.nVdx/AYrTV8BiqM6emzeWq.5HeP0i5y	0.00	user
4	michael	michael@mail.com	$2a$10$6wtrPTdKpngx2Jz7A5ZNNe9dP0tJGto18zHjsCOXiuLZgAQ/CoWOm	10.00	user
\.


--
-- TOC entry 4966 (class 0 OID 0)
-- Dependencies: 220
-- Name: transaction_id_seq; Type: SEQUENCE SET; Schema: public; Owner: myappuser
--

SELECT pg_catalog.setval('public.transaction_id_seq', 5, true);


--
-- TOC entry 4967 (class 0 OID 0)
-- Dependencies: 222
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: myappuser
--

SELECT pg_catalog.setval('public.users_id_seq', 4, true);


--
-- TOC entry 4794 (class 2606 OID 16545)
-- Name: connexion connexion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connexion
    ADD CONSTRAINT connexion_pkey PRIMARY KEY (user_id, connected_to_user);


--
-- TOC entry 4796 (class 2606 OID 16547)
-- Name: transaction transaction_pkey; Type: CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);


--
-- TOC entry 4798 (class 2606 OID 16549)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 4800 (class 2606 OID 16551)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4802 (class 2606 OID 16553)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4803 (class 2606 OID 16554)
-- Name: connexion fk_connexion_connected_to_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connexion
    ADD CONSTRAINT fk_connexion_connected_to_user FOREIGN KEY (connected_to_user) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4804 (class 2606 OID 16559)
-- Name: connexion fk_connexion_user_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.connexion
    ADD CONSTRAINT fk_connexion_user_id FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4805 (class 2606 OID 16564)
-- Name: transaction fk_transaction_from_user; Type: FK CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT fk_transaction_from_user FOREIGN KEY (from_user_id) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- TOC entry 4806 (class 2606 OID 16569)
-- Name: transaction fk_transaction_to_user; Type: FK CONSTRAINT; Schema: public; Owner: myappuser
--

ALTER TABLE ONLY public.transaction
    ADD CONSTRAINT fk_transaction_to_user FOREIGN KEY (to_user_id) REFERENCES public.users(user_id) ON DELETE SET NULL;


--
-- TOC entry 4963 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE connexion; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.connexion TO myappuser;


--
-- TOC entry 2090 (class 826 OID 16574)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO myappuser;


-- Completed on 2025-07-15 21:36:26

--
-- PostgreSQL database dump complete
--

