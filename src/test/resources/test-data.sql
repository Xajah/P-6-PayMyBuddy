TRUNCATE TABLE connexion, transaction, users RESTART IDENTITY CASCADE;

-- users: user_id au lieu de id !
INSERT INTO users (user_id, username, email, password, solde) VALUES
  (1, 'alice', 'alice@mail.com', '$2a$06$SqUY61I8leTmRy/lhk8kVOw/krMu4QcSTHPJcncJpqySUhIkr2vBS', 100.00),
  (2, 'bob', 'bob@mail.com', '$2a$06$s14RrQoxK40v4x.gsZEcROqKDr4WFKQOnYu09GuGThDYYTnCf9RHy', 100.00),
  (3, 'carol', 'carol@mail.com', '$2a$06$K4Bxqlhy80w8gWqb6gjCw.nVdx/AYrTV8BiqM6emzeWq.5HeP0i5y', 100.00);

-- connexion : pas modifié
INSERT INTO connexion (user_id, connected_to_user) VALUES
  (1, 2),
  (1, 3),
  (2, 3);

-- transaction : from_user_id/to_user_id sont ok, mais id de transaction doit-il être id ou transaction_id ? Vérifie ton schéma !
INSERT INTO transaction (id, description, amount, from_user_id, to_user_id) VALUES
  (1, 'Achat de livre', 26.35, 1, 2),
  (2, 'Remboursement dîner', 42.50, 2, 3),
  (3, 'Partage abonnement', 18.00, 3, 1),
  (4, 'Dépannage voiture', 19.99, 2, 1),
  (5, 'Billets de concert', 70.00, 3, 2);

-- les SEQUENCES doivent aussi être au bon nom : users_id_seq sur user_id, pas id
SELECT setval('transaction_id_seq', (SELECT MAX(id) FROM transaction));
SELECT setval('users_id_seq', (SELECT MAX(user_id) FROM users));
