(function () {

   sessionStorage.pendingPageNumber = 1;
   prepareTableRequest("#pending_tbody");
   document.querySelector("#pending_select").addEventListener("click", () => { sessionStorage.pendingPageNumber = 1; })
   document.querySelector("#update_pending").addEventListener("click", () => { prepareTableRequest("#pending_tbody"); })

   function prepareTableRequest(tableBodyselector) {
      const url = "http://localhost:8080/ers/api/reimbursements";
      const columnMap = `status=PENDING&e_id=${sessionStorage.managerEmployeeId}&`;
      const rowLimit = document.querySelector("#pending_select").value;
      const pageNumber = sessionStorage.pendingPageNumber;
      const orderBy = "r_id ASC";
      const offset = rowLimit * (pageNumber - 1);
      populateTable(url, columnMap, orderBy, rowLimit, offset, tableBodyselector, addRowToTable, setTableTitle, undefined, prepareTableRequest);
   }

   function addRowToTable(data, tableBodyselector) {
      let row =
         `<tr><td>$${data.amount}</td>
      <td>${data.status}</td>
      <td>${data.dateSubmitted}</td>
      <td><button id="approve_${data.id}" type="button" class="btn btn-primary btn-center btn-size">Approve</button></td>
		<td><button id="deny_${data.id}" type="button" class="btn btn-primary btn-center btn-size">Deny</button></td></tr>`;
      document.querySelector(tableBodyselector).insertAdjacentHTML("beforeend", row);

      let jwt = jwt_decode(sessionStorage.ers_auth);
      let currentEmail = jwt.email;
      let approvedMap = `id=${data.id}&email=${currentEmail}&state=${"APPROVED"}`;
      let deniedMap = `id=${data.id}&email=${currentEmail}&state=${"DENIED"}`;
      document.querySelector(`#approve_${data.id}`).addEventListener("click", () => { approve(approvedMap, prepareTableRequest.bind(this, tableBodyselector)); });
      document.querySelector(`#deny_${data.id}`).addEventListener("click", () => { approve(deniedMap, prepareTableRequest.bind(this, tableBodyselector)); });
   }

   function setTableTitle(data, tableBodyselector) {
      let tableTitle = document.querySelector(tableBodyselector).parentElement.parentElement.firstElementChild;
      tableTitle.innerHTML = data.employeeAccount.firstName + " " + data.employeeAccount.lastName;
   }

})();