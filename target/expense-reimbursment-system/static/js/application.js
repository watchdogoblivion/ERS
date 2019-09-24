window.onload = function () {
  window.onpopstate = manageHistory;
  if (history.state != undefined) {
    dispatchBy("HISTORY");
  } else {
    bootstrap();
  }
};

function manageHistory(e) {
  if (e.state != undefined) {
    loadResources(e.state.pageResource["main_content"], e.state.pageResource["header"], e.state.pageResource["footer"])
  }

}

function dispatchBy(type) {
  let ers_auth;
  let jwt;
  if ((ers_auth = sessionStorage.ers_auth) != undefined && (jwt = jwt_decode(ers_auth)).EXP > new Date().getTime()) {
    const resources = JSON.parse(window.localStorage.getItem("Resources"));
    let authority = jwt.authority;
    switch (type.toUpperCase()) {
      case "AUTHORITY": dispatchByAuthority(authority, resources);
        break;
      case "HISTORY": dispatchByHistory(authority, resources);
        break;
    }
  } else {
    goToPage("login");
  }
}

function dispatchByAuthority(authority, resources) {
  switch (authority.toUpperCase()) {
    case "MANAGER":
      addToHistory(resources.managerHome, resources.managerHeader, resources.applicationFooter);
      loadResources(resources.managerHome, resources.managerHeader, resources.applicationFooter);
      break;
    case "EMPLOYEE":
      addToHistory(resources.employeeHome, resources.employeeHeader, resources.applicationFooter);
      loadResources(resources.employeeHome, resources.employeeHeader, resources.applicationFooter);
      break;
  }
}

function dispatchByHistory(authority) {
  let title = history.state.pageResource["main_content"].title.toUpperCase();
  switch (authority.toUpperCase()) {
    case "MANAGER":
      if (title.startsWith("MANAGER")) {
        loadResources(history.state.pageResource["main_content"], history.state.pageResource["header"], history.state.pageResource["footer"]);
      }
      else {
        history.go(-(history.state.pageResource.pageNumber));
        bootstrap();
      }
      break;
    case "EMPLOYEE":
      if (title.startsWith("EMPLOYEE")) {
        loadResources(history.state.pageResource["main_content"], history.state.pageResource["header"], history.state.pageResource["footer"]);
      }
      else {
        history.go(-(history.state.pageResource.pageNumber));
        bootstrap();
      }
      break;
  }
}

function replaceHistory(main_content, optionalHeader, optionalFooter) {
  history.replaceState({ pageResource: { "pageNumber": history.state == undefined ? 1 : history.state.pageResource.pageNumber + 1, "main_content": main_content, "header": optionalHeader, "footer": optionalFooter } }, main_content.title, `?page=${main_content.title}`);
}

function addToHistory(main_content, optionalHeader, optionalFooter) {
  if (main_content != undefined && main_content.title != "Login") {
    history.pushState({ pageResource: { "pageNumber": history.state == undefined ? 1 : history.state.pageResource.pageNumber + 1, "main_content": main_content, "header": optionalHeader, "footer": optionalFooter } }, main_content.title, `?page=${main_content.title}`);
  }
}

function loadResources(resource, optionalHeader, optionalFooter) {
  loadCSS(resource, optionalHeader, optionalFooter);
  loadHTML({ "#main_content": resource }, { "#header": optionalHeader }, { "#footer": optionalFooter });
  loadJS(resource, optionalHeader, optionalFooter);
}

function loadCSS() {
  let styleTag = document.getElementsByTagName("style")[0];
  styleTag.innerHTML = "";
  for (const resource of arguments) {
    let css = resource != undefined ? (resource.css != undefined ? resource.css : "") : "";
    styleTag.innerHTML += css + " ";
  }
}

function loadHTML() {
  for (const item of arguments) {
    for (const selector in item) {
      if (item.hasOwnProperty(selector)) {
        let resource = item[selector];
        if (selector == "#main_content") {
          let htmlTitle = resource != undefined ? (resource.title != undefined ? resource.title : "") : "";
          let titleTag = document.getElementsByTagName("title")[0];
          titleTag.innerHTML = `ERS - ${htmlTitle}`;
        }
        let htmlResource = resource != undefined ? (resource.html != undefined ? resource.html : "") : "";
        let htmlDiv = document.querySelector(selector);
        htmlDiv.innerHTML = htmlResource;
      }
    }
  }
}

function loadJS() {
  let oldScriptTag = document.querySelector("#scripts");
  oldScriptTag.parentNode.removeChild(oldScriptTag);
  let scriptTag = document.createElement("script");
  scriptTag.id = "scripts";
  scriptTag.type = "text/javascript";
  for (const resource of arguments) {
    let js = resource != undefined ? (resource.javascript != undefined ? resource.javascript : "") : "";
    scriptTag.innerHTML += js + " ";
  }
  document.body.appendChild(scriptTag);
}

function goToPage(main, header, footer) {
  const resources = JSON.parse(window.localStorage.getItem("Resources"));
  addToHistory(resources[main], resources[header], resources[footer]);
  loadResources(resources[main], resources[header], resources[footer]);
}

function bootstrap() {
  let url = `http://localhost:8080/ers/api/resources`;
  let xhr = new XMLHttpRequest();
  xhr.open("GET", url);
  xhr.responseType = "json";
  xhr.onload = function () {
    let status = xhr.status;
    if (status == 200) {
      window.localStorage.setItem("Resources", JSON.stringify(xhr.response));
      dispatchBy("AUTHORITY");
    } else {
      console.log("Recieved status code: " + xhr.status);
    }
  };
  xhr.send();
}

function logout() {
  sessionStorage.removeItem("ers_auth");
  sessionStorage.removeItem("imageUrl");
  sessionStorage.removeItem("resolvedPageNumber")
  sessionStorage.removeItem("pendingPageNumber")
  window.location.href = `http://localhost:8080/ers/`;
}

function getUserAccounts(params, callback) {
  if (params == undefined) { params = "" }
  let url = `http://localhost:8080/ers/api/user-accounts?${params}`;
  let xhr = new XMLHttpRequest();
  xhr.open("GET", url);
  xhr.responseType = "json";
  xhr.setRequestHeader("Authorization", JSON.parse(sessionStorage.ers_auth));
  xhr.onload = function () {
    let status = xhr.status;
    if (status == 200) {
      console.log("Retrieve Successful")
      if (callback != undefined) {
        data = xhr.response;
        callback(data[1]);
      }
    } else if (status == 401) {
      console.log("Authorization required");
      goToPage("login")
    } {
      console.log("Recieved status code: " + xhr.status);
    }
  };
  xhr.send();
}

function setProfileImage() {
  if (sessionStorage.imageUrl != undefined && sessionStorage.imageUrl != "") {
    for (const selector of arguments) {
      document.querySelector(selector).style.display = "inline";
      document.querySelector(selector).src = sessionStorage.imageUrl;
    }
    let elements = document.getElementsByClassName("user_circle");
    for (const element of elements) {
      element.style.display = "none";
    }

  } else {
    for (const selector of arguments) {
      document.querySelector(selector).style.display = "none";
    }
    let elements = document.getElementsByClassName(".user_circle");
    for (const element of elements) {
      element.style.display = "inline";
    }
  }
}

function populateTable(base_url, columnMap, ORDERBY, LIMIT, OFFSET, tableBodyselector, callback, optionalCallback, loopOptionalCallback, outerCallback) {
  if (columnMap == undefined) { columnMap = ""; }
  let url = `${base_url}?${columnMap}ORDERBY=${ORDERBY}&LIMIT=${LIMIT}&OFFSET=${OFFSET}`;
  let xhr = new XMLHttpRequest();
  xhr.open("GET", url);
  xhr.responseType = "json";
  xhr.setRequestHeader("Authorization", JSON.parse(sessionStorage.ers_auth));
  xhr.onload = function () {
    let status = xhr.status;
    if (status == 200) {
      console.log("Retrieve Successful")
      addRows(xhr.response, tableBodyselector, callback, optionalCallback, loopOptionalCallback, outerCallback);
    } else if (status == 401) {
      console.log("Authorization required");
      goToPage("login")
    } else {
      console.log("Recieved status code: " + xhr.status);
    }
  };
  xhr.send();
}


function addRows(jsonObject, tableBodyselector, callback, optionalCallback, loopOptionalCallback, outerCallback) {
  let databaseRowTotal = jsonObject[0];
  let dataArray = jsonObject[1];
  
  document.querySelector(tableBodyselector).innerHTML = "";
  if (outerCallback != undefined) {
    addPagination(databaseRowTotal, tableBodyselector, outerCallback);
  }
  for (let data of dataArray) {
    if (optionalCallback != undefined) {
      if (!loopOptionalCallback) {
        loopOptionalCallback = true; optionalCallback(data, tableBodyselector);
      } else {
        optionalCallback(data, tableBodyselector);
      }
    }
    callback(data, tableBodyselector);
  }
};

function addPagination(databaseRowTotal, tableBodyselector, prepareTableRequest) {

  let pagination, pages, type, rowLimit, btn, elem;
  switch (tableBodyselector) {
    case "#pending_tbody":
      type = "pending_select";
      rowLimit = document.querySelector(`#${type}`).value;
      pages = Math.ceil(databaseRowTotal / rowLimit) + 1;
      pagination = document.querySelector("#pending_pagination");
      break;
    case "#resolved_tbody":
      type = "resolved_select";
      rowLimit = document.querySelector(`#${type}`).value;
      pages = Math.ceil(databaseRowTotal / rowLimit) + 1;
      pagination = document.querySelector("#resolved_pagination");
      break;
    case "#all_employees_tbody":
      type = "all_employees_select";
      rowLimit = document.querySelector(`#${type}`).value;
      pages = Math.ceil(databaseRowTotal / rowLimit) + 1;
      pagination = document.querySelector("#all_employees_pagination");;
      break;
  }

  pagination.innerHTML = "";

  for (let i = 1; i < pages; i++) {
    let listItem = document.createElement("li");
    listItem.innerHTML = `<button id="${type}${i}" type="button" class="btn paginated">${i}</button>`;
    listItem.class = "page-item";
    pagination.insertAdjacentElement("beforeend", listItem);
    document.querySelector(`#${type}${i}`).addEventListener("click", () => {
      switch (tableBodyselector) {
        case "#pending_tbody":
          btn = document.querySelector(`#${type}${sessionStorage.pendingPageNumber}`);
          if (btn != undefined) {
            document.querySelector(`#${type}${sessionStorage.pendingPageNumber}`).classList.remove("active-btn");
          }
          sessionStorage.pendingPageNumber = i;
          prepareTableRequest("#pending_tbody")
          break;
        case "#resolved_tbody":
          btn = document.querySelector(`#${type}${sessionStorage.resolvedPageNumber}`);
          if (btn != undefined) {
            document.querySelector(`#${type}${sessionStorage.resolvedPageNumber}`).classList.remove("active-btn");
          }
          sessionStorage.resolvedPageNumber = i;
          prepareTableRequest("#resolved_tbody")
          break;
        case "#all_employees_tbody":
          btn = document.querySelector(`#${type}${sessionStorage.allEmployeesPageNumber}`);
          if (btn != undefined) {
            document.querySelector(`#${type}${sessionStorage.allEmployeesPageNumber}`).classList.remove("active-btn");
          }
          sessionStorage.allEmployeesPageNumber = i;
          prepareTableRequest("#all_employees_tbody")
          break;
      }
    });
  }
  switch (tableBodyselector) {
    case "#pending_tbody":
      elem = document.querySelector(`#${type}${sessionStorage.pendingPageNumber}`);
      if (elem != undefined) { elem.classList.add("active-btn") }
      break;
    case "#resolved_tbody":
      elem = document.querySelector(`#${type}${sessionStorage.resolvedPageNumber}`);
      if (elem != undefined) { elem.classList.add("active-btn") }
      break;
      case "#all_employees_tbody":
      elem = document.querySelector(`#${type}${sessionStorage.allEmployeesPageNumber}`);
      if (elem != undefined) { elem.classList.add("active-btn") }
      break;
  }
}

