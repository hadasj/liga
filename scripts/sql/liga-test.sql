select z.id,  h1.name, h2.name, char(k.od), k.do
from zapas z
join kolo k on k.id = z.kolo
join hrac h1 on h1.id = z.hrac1
join hrac h2 on h2.id = z.hrac2
order by z.id;

