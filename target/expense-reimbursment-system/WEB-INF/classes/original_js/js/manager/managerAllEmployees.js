(function () {

	sessionStorage.allEmployeesPageNumber = 1;
	prepareTableRequest("#all_employees_tbody");
	document.querySelector("#all_employees_select").addEventListener("click", () => { sessionStorage.allEmployeesPageNumber = 1; })
	document.querySelector("#update_all_employees").addEventListener("click", () => { prepareTableRequest("#all_employees_tbody"); })

	function prepareTableRequest(tableBodyselector) {
		const url = "http://localhost:8080/ers/api/user-accounts";
		const columnMap = "authority=EMPLOYEE&";
		const orderBy = "ua_id ASC";
		const rowLimit = document.querySelector("#all_employees_select").value;
		let pageNumber = sessionStorage.allEmployeesPageNumber;
		const offset = rowLimit * (pageNumber - 1);
		populateTable(url, columnMap, orderBy, rowLimit, offset, tableBodyselector, addRowToTable, undefined, undefined, prepareTableRequest);
	}

	function addRowToTable(data, tableBodyselector) {
		let row =
			`<tr><td><button id="id_${data.id}" type="button" class="btn btn-link">${data.email}</button></td>
			<td>${data.firstName} ${data.lastName}</td>`;
		document.querySelector(tableBodyselector).insertAdjacentHTML("beforeend", row);
		document.querySelector(`#id_${data.id}`).addEventListener("click", () => { sessionStorage.managerEmployeeId = data.id; goToPage("managerEmployee", "managerHeader", "applicationFooter"); });
	}

})();