--
-- PostgreSQL database dump
--

\restrict b6X9kyYC0m53bMCQFxAmrjVuaquTJQ3hrbLwlbowAkUIH7wQW1o2OzWQweDHPf6

-- Dumped from database version 16.14 (Debian 16.14-1.pgdg13+1)
-- Dumped by pg_dump version 16.14 (Debian 16.14-1.pgdg13+1)

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

ALTER TABLE IF EXISTS ONLY public.leaderboard_results DROP CONSTRAINT IF EXISTS leaderboard_results_pkey;
ALTER TABLE IF EXISTS public.leaderboard_results ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS public.leaderboard_results_id_seq;
DROP TABLE IF EXISTS public.leaderboard_results;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: leaderboard_results; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.leaderboard_results (
    id bigint NOT NULL,
    nickname character varying(64) NOT NULL,
    player_count smallint NOT NULL,
    final_score integer NOT NULL,
    remaining_food integer NOT NULL,
    played_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT leaderboard_results_player_count_check CHECK (((player_count >= 2) AND (player_count <= 5)))
);


--
-- Name: leaderboard_results_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.leaderboard_results_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: leaderboard_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.leaderboard_results_id_seq OWNED BY public.leaderboard_results.id;


--
-- Name: leaderboard_results id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leaderboard_results ALTER COLUMN id SET DEFAULT nextval('public.leaderboard_results_id_seq'::regclass);


--
-- Data for Name: leaderboard_results; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.leaderboard_results (id, nickname, player_count, final_score, remaining_food, played_at) FROM stdin;
1	rick	2	72	0	2026-05-10 23:29:59.950896
2	rick2	2	48	0	2026-05-10 23:29:59.950896
3	rick	2	22	0	2026-05-10 23:43:41.924902
4	rick2	2	10	0	2026-05-10 23:43:41.924902
5	rick2	2	35	0	2026-05-12 00:36:56.87977
6	rick	2	4	0	2026-05-12 00:36:56.87977
7	rick2	2	1	6	2026-05-12 14:31:37.589216
8	rick	2	-1	0	2026-05-12 14:31:37.589216
9	rick2	2	3	4	2026-05-12 18:02:06.27807
10	rick	2	2	1	2026-05-12 18:02:06.27807
11	rick	2	5	2	2026-05-12 18:07:53.150609
12	rick2	2	-2	4	2026-05-12 18:07:53.150609
13	rick2	2	1	0	2026-05-12 18:11:38.241539
14	rick	2	0	2	2026-05-12 18:11:38.241539
15	rick	2	3	4	2026-05-12 18:14:15.387211
16	rick2	2	1	1	2026-05-12 18:14:15.387211
17	rick	2	1	4	2026-05-12 18:21:23.841901
18	rick2	2	0	1	2026-05-12 18:21:23.841901
19	rick3	2	-2	4	2026-05-12 18:30:00.576693
20	rick4	2	-2	2	2026-05-12 18:30:00.576693
21	rick2	2	4	2	2026-05-12 18:35:00.858729
22	rick	2	-1	0	2026-05-12 18:35:00.858729
23	rick4	2	1	1	2026-05-12 18:35:34.864907
24	rick3	2	0	5	2026-05-12 18:35:34.864907
25	rick	2	3	1	2026-05-14 09:43:10.957058
26	rick2	2	-1	6	2026-05-14 09:43:10.957058
27	rick	2	1	1	2026-05-14 09:45:11.147065
28	rick2	2	-2	4	2026-05-14 09:45:11.147065
29	rick	2	3	6	2026-05-14 10:06:06.903802
30	rick2	2	3	1	2026-05-14 10:06:06.903802
31	rick	2	11	0	2026-05-14 10:10:37.285297
32	rick2	2	6	2	2026-05-14 10:10:37.285297
33	rick	2	3	2	2026-05-14 10:57:10.786319
34	rick2	2	1	4	2026-05-14 10:57:10.786319
35	rick2	2	1	4	2026-05-14 10:58:33.705256
36	rick	2	1	1	2026-05-14 10:58:33.705256
37	rick2	2	5	0	2026-05-14 11:03:54.38288
38	rick	2	2	3	2026-05-14 11:03:54.38288
39	rick2	2	2	2	2026-05-14 11:09:23.279922
40	rick	2	0	1	2026-05-14 11:09:23.279922
41	rick2	2	1	0	2026-05-14 11:15:42.029546
42	rick	2	-2	4	2026-05-14 11:15:42.029546
43	rick2	2	1	2	2026-05-14 11:17:59.167486
44	rick	2	1	0	2026-05-14 11:17:59.167486
45	rick2	2	2	1	2026-05-14 11:19:55.03419
46	rick	2	0	5	2026-05-14 11:19:55.03419
47	rick2	2	12	1	2026-05-14 11:29:09.101104
48	rick	2	6	4	2026-05-14 11:29:09.101104
49	rick	2	4	4	2026-05-14 13:37:10.081819
50	rick2	2	1	1	2026-05-14 13:37:10.081819
51	rick2	2	4	2	2026-05-14 14:02:53.069718
52	rick	2	0	0	2026-05-14 14:02:53.069718
53	rick2	2	0	2	2026-05-14 15:17:22.801617
54	rick	2	0	0	2026-05-14 15:17:22.801617
55	rick2	2	1	4	2026-05-14 16:49:08.419027
56	rick	2	1	1	2026-05-14 16:49:08.419027
57	rick2	2	0	2	2026-05-14 21:29:34.405588
58	rick	2	0	0	2026-05-14 21:29:34.405588
59	rick2	2	0	5	2026-05-14 23:00:50.232478
60	rick	2	-1	4	2026-05-14 23:00:50.232478
61	rick	2	5	4	2026-05-14 23:18:34.238656
62	rick2	2	2	1	2026-05-14 23:18:34.238656
63	rick	2	1	4	2026-05-14 23:23:26.588909
64	rick2	2	1	1	2026-05-14 23:23:26.588909
65	rick2	2	2	1	2026-05-14 23:25:54.154705
66	rick	2	0	2	2026-05-14 23:25:54.154705
67	rick	2	78	7	2026-05-15 23:22:07.087108
68	rick2	2	56	4	2026-05-15 23:22:07.087108
69	rick	2	73	0	2026-05-19 00:09:52.573761
70	rick2	2	61	0	2026-05-19 00:09:52.573761
71	rick2	2	66	0	2026-05-19 00:13:18.447905
72	rick	2	16	0	2026-05-19 00:13:18.447905
73	rick	2	2	6	2026-05-19 00:27:54.504054
74	rick2	2	0	1	2026-05-19 00:27:54.504054
75	rick2	2	1	4	2026-05-19 14:24:06.342221
76	rick	2	-1	1	2026-05-19 14:24:06.342221
77	rick2	2	5	4	2026-05-19 14:25:25.155173
78	rick	2	-2	1	2026-05-19 14:25:25.155173
79	rick	2	38	0	2026-06-10 14:38:03.993558
80	rick2	2	36	0	2026-06-10 14:38:03.993558
81	cbg	2	86	2	2026-06-10 15:12:58.971025
82	rick	2	77	4	2026-06-10 15:12:58.971025
\.


--
-- Name: leaderboard_results_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.leaderboard_results_id_seq', 82, true);


--
-- Name: leaderboard_results leaderboard_results_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leaderboard_results
    ADD CONSTRAINT leaderboard_results_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict b6X9kyYC0m53bMCQFxAmrjVuaquTJQ3hrbLwlbowAkUIH7wQW1o2OzWQweDHPf6

