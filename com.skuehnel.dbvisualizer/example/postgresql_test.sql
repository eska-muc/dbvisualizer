--
-- PostgreSQL database dump
--

-- Dumped from database version 14.1
-- Dumped by pg_dump version 14.1

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
-- Name: test; Type: SCHEMA; Schema: -; Owner: test
--

CREATE SCHEMA test;


ALTER SCHEMA test OWNER TO test;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: test; Owner: test
--

CREATE TABLE test.address (
    pk_address_id integer NOT NULL,
    street character varying(80),
    house_no character varying(10),
    city character varying(80),
    state_province character varying(80),
    postal_code character varying(20),
    country character varying(80)
);


ALTER TABLE test.address OWNER TO test;

--
-- Name: TABLE address; Type: COMMENT; Schema: test; Owner: test
--

COMMENT ON TABLE test.address IS 'Address table. Many persons may have the same address.';


--
-- Name: COLUMN address.pk_address_id; Type: COMMENT; Schema: test; Owner: test
--

COMMENT ON COLUMN test.address.pk_address_id IS 'Primary Key of address';


--
-- Name: COLUMN address.street; Type: COMMENT; Schema: test; Owner: test
--

COMMENT ON COLUMN test.address.street IS 'Street part of an address, if applicable';


--
-- Name: person; Type: TABLE; Schema: test; Owner: test
--

CREATE TABLE test.person (
    pk_person_id integer NOT NULL,
    first_name character varying(80) NOT NULL,
    last_name character varying(80) NOT NULL,
    date_of_birth date NOT NULL
);


ALTER TABLE test.person OWNER TO test;

--
-- Name: person_address; Type: TABLE; Schema: test; Owner: test
--

CREATE TABLE test.person_address (
    fk_adress_id integer,
    fk_person_id integer
);


ALTER TABLE test.person_address OWNER TO test;

--
-- Data for Name: address; Type: TABLE DATA; Schema: test; Owner: test
--

COPY test.address (pk_address_id, street, house_no, city, state_province, postal_code, country) FROM stdin;
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: test; Owner: test
--

COPY test.person (pk_person_id, first_name, last_name, date_of_birth) FROM stdin;
\.


--
-- Data for Name: person_address; Type: TABLE DATA; Schema: test; Owner: test
--

COPY test.person_address (fk_adress_id, fk_person_id) FROM stdin;
\.


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: test; Owner: test
--

ALTER TABLE ONLY test.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (pk_address_id);


--
-- Name: person person_pkey; Type: CONSTRAINT; Schema: test; Owner: test
--

ALTER TABLE ONLY test.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (pk_person_id);


--
-- Name: person_address person_address_fk_adress_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: test
--

ALTER TABLE ONLY test.person_address
    ADD CONSTRAINT person_address_fk_adress_id_fkey FOREIGN KEY (fk_adress_id) REFERENCES test.address(pk_address_id);


--
-- Name: person_address person_address_fk_person_id_fkey; Type: FK CONSTRAINT; Schema: test; Owner: test
--

ALTER TABLE ONLY test.person_address
    ADD CONSTRAINT person_address_fk_person_id_fkey FOREIGN KEY (fk_person_id) REFERENCES test.person(pk_person_id);


--
-- PostgreSQL database dump complete
--

