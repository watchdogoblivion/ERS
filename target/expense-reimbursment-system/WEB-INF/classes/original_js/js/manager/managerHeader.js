(function () {

  document.querySelector("#home").addEventListener("click", () => { goToPage("managerHome", "managerHeader", "applicationFooter"); });
  document.querySelector("#all-employees").addEventListener("click", () => { goToPage("managerAllEmployees", "managerHeader", "applicationFooter"); });
  document.querySelector("#register-employee").addEventListener("click", () => { goToPage("managerRegisterEmployee", "managerHeader", "applicationFooter"); });
  document.querySelector("#logout").addEventListener("click", logout);

})();


function approve(map, callback) {
  let url = `http://localhost:8080/ers/api/reimbursements?${map}`;
  let xhr = new XMLHttpRequest();
  xhr.open("PUT", url);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.setRequestHeader("Authorization", JSON.parse(sessionStorage.ers_auth));
  xhr.onload = function () {
    let status = xhr.status;
    if (status == 200) {
      console.log("Reimbursement update Successful");
      callback();
    } else if (status == 401) {
      console.log("Authorization required");
      goToPage("login")
    } else {
      console.log("Recieved status code: " + xhr.status);
    }
  };
  xhr.send();
}


