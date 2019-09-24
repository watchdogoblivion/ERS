(function () {

	sessionStorage.pendingPageNumber = 1;
	sessionStorage.resolvedPageNumber = 1;
	prepareTableRequest("#pending_tbody");
	prepareTableRequest("#resolved_tbody");
	document.querySelector("#pending_select").addEventListener("click", () => { sessionStorage.pendingPageNumber = 1; sessionStorage.resolvedPageNumber = 1; })
	document.querySelector("#resolved_select").addEventListener("click", () => { sessionStorage.pendingPageNumber = 1; sessionStorage.resolvedPageNumber = 1; })
	document.querySelector("#update_pending").addEventListener("click", () => { prepareTableRequest("#pending_tbody"); })
	document.querySelector("#update_resolved").addEventListener("click", () => { prepareTableRequest("#resolved_tbody"); })
	document.querySelector("#submit_reimbursment").addEventListener("click", submitReimbursement);

	function prepareTableRequest(tableBodyselector) {
		let jwt = jwt_decode(sessionStorage.ers_auth);
		const url = "http://localhost:8080/ers/api/reimbursements";
		let columnMap;
		let rowLimit;
		let pageNumber;
		switch (tableBodyselector) {
			case "#pending_tbody":
				columnMap = `email=${jwt.email}&status=PENDING&`;
				rowLimit = document.querySelector("#pending_select").value;
				pageNumber = sessionStorage.pendingPageNumber;
				break;
			case "#resolved_tbody": columnMap = `email=${jwt.email}&status=RESOLVED&`;
				rowLimit = document.querySelector("#resolved_select").value;
				pageNumber = sessionStorage.resolvedPageNumber;
				break;
		}
		const orderBy = "r_id ASC";
		const offset = rowLimit * (pageNumber - 1);
		populateTable(url, columnMap, orderBy, rowLimit, offset, tableBodyselector, addRowToTable, undefined, undefined, prepareTableRequest);
	}

	function submitReimbursement(e) {
		e.preventDefault();
		let reimbursment_amount = document.querySelector("#reimbursment_amount").value;
		let url = `http://localhost:8080/ers/api/reimbursements`;
		let xhr = new XMLHttpRequest();
		xhr.open("POST", url);
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		xhr.setRequestHeader("Authorization", JSON.parse(sessionStorage.ers_auth));
		xhr.onload = function () {
			document.querySelector("#reimbursment_info").innerHTML = "";
			if (xhr.status == 200) {
				document.querySelector("#reimbursment_info").innerHTML = `Reimbursement Submitted Succesfully. Amount: $${reimbursment_amount} `;
				prepareTableRequest("#pending_tbody");
			} else {
				console.log("Recieved status code: " + xhr.status + " " + xhr.statusText);
				if (xhr.getResponseHeader("Authorization") == null) {
					console.log("Failed to authenticate")
					sessionStorage.ers_auth = null;
					goToPage("login");
				}
			}
		};
		xhr.send(`reimbursment_amount=${reimbursment_amount}`);
	}

	function addRowToTable(data, tableBodyselector) {

		let row = `<tr>
        <td>$${data.amount}</td>
		<td>${data.status}</td>`;
		if (tableBodyselector == "#resolved_tbody") {
			row += `<td>${data.state}</td>`;
		}
		row += `<td>${data.dateSubmitted}</td>`
		if (tableBodyselector == "#resolved_tbody") {
			row += `<td>${data.managerAccount.firstName + " " + data.managerAccount.lastName}</td></tr>`;
		}
		document.querySelector(tableBodyselector).innerHTML += row;

	}

})();