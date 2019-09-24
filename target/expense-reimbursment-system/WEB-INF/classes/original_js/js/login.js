(function () {
  document.querySelector("#submit-login").addEventListener("click", validateLogin);
  function validateLogin(e) {
    e.preventDefault();
    document.querySelector("#warning").textContent = "";
    let email = document.querySelector("#email").value;
    let password = document.querySelector("#password").value;
    let url = `http://localhost:8080/ers/login`;
    let xhr = new XMLHttpRequest();
    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onload = function () {
      if (xhr.status == 200) {
        let ers_auth = xhr.getResponseHeader("Authorization");
        if (ers_auth) {
          sessionStorage.ers_auth = JSON.stringify(ers_auth);
          sessionStorage.imageUrl = xhr.response;
          console.log(sessionStorage.imageUrl)
          if(history.state != undefined){
            dispatchBy("HISTORY");
          }else {
            dispatchBy("AUTHORITY");
          }  
        } else {
          document.querySelector("#warning").textContent = "Invalid Credentitals";
        }
      } else if (xhr.status == 401) {
        console.log("Recieved status code: " + xhr.status + " : " + "Your session is expired");
      } else {
        console.log("Recieved status code: " + xhr.status);
      }
    };
    xhr.send(`email=${email}&password=${password}`);
  }
})();

