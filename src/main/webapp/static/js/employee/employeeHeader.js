(function () {

  document.querySelector("#home").addEventListener("click", ()=> { goToPage("employeeHome", "employeeHeader", "applicationFooter"); });
  document.querySelector("#employeeProfile").addEventListener("click", () => { goToPage("employeeProfile", "employeeHeader", "applicationFooter"); });
  document.querySelector("#logout").addEventListener("click", logout);
  setProfileImage("#mini_profile_image");

})();



