function modifyForUpdateCoupon(id, sconto, cf_creatore) {
    obj = id;
    document.getElementById('sconto').value = sconto;
    document.getElementById('CF_Creatore').value = cf_creatore;
    document.getElementById('creaModal').style.display = 'block';
    document.getElementById('btn-crea').style.display = 'none';
    createBtn();
}

function createBtn(){
    if (obj != null)
    {
        document.getElementById("btnDiv").innerHTML =
            '<button type="submit" formaction="CouponManager/updateCoupon" class="btn btn-verde">Salva Modifiche</button>' +
            '<button type="submit" formaction="CouponManager/deleteCoupon" class="btn btn-primary">Elimina Coupon</button>';
        document.getElementById("idCoupon").value = obj;
        obj = null;
    }
    else
        document.getElementById("btnDiv").innerHTML = '<button type="submit" id="btn2" class="btn btn-primary" formaction="CouponManager/creaCoupon">Crea Coupon</button>';
}