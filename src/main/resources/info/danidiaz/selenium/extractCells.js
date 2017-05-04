let xss = [];
let rows = arguments[0].getElementsByTagName('tr');
for (let ri = 0; ri < rows.length; ri++) {
    let xs = [];
    let row = rows[ri];
    let cells = row.getElementsByTagName('td');
    for (let ci = 0; ci < cells.length; ci++) {
         let cell = cells[ci];
         xs.push(cell.innerText);
    };
    xss.push(xs);
}
return xss;

