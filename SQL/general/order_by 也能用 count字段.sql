-- count(PRSNTTN_ID) 这个列，在 order by 时，也可以用
select count(PRSNTTN_ID) eachPrsnttnCount, PRSNTTN_ID from RESOURC group by PRSNTTN_ID
order by eachPrsnttnCount desc;
