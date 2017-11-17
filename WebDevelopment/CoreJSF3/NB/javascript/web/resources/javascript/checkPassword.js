function checkPassword(form) {
   var password = form[form.id + ":password"].value;
   var passwordConfirm = form[form.id + ":passwordConfirm"].value;

   if(password == passwordConfirm)
      form.submit();
   else
      alert("Password and password confirm fields don't match");
}
