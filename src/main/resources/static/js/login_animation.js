
let idx = 0;

let image_List = ["mainA_1.jpg", "mainA_2.jpg", "mainA_3.jpg", "mainA_4.jpg"];
let image_animation = document.querySelectorAll(".image_animation");

console.log(image_animation);
function changeImage() {


    image_animation[idx].classList.add("change_animation");

    if (idx === 0) {
        image_animation[3].classList.remove("change_animation");
        image_animation[2].classList.remove("change_animation2");
        image_animation[3].classList.add("change_animation2");
    } else if (idx === 1) {
        image_animation[0].classList.remove("change_animation");
        image_animation[3].classList.remove("change_animation2");
        image_animation[0].classList.add("change_animation2");
    } else if (idx === 2) {
        image_animation[1].classList.remove("change_animation");
        image_animation[0].classList.remove("change_animation2");
        image_animation[1].classList.add("change_animation2");
    } else {
        image_animation[2].classList.remove("change_animation");
        image_animation[1].classList.remove("change_animation2");
        image_animation[2].classList.add("change_animation2");
    }
    idx++;
    if (idx >= image_List.length) idx = 0;
}

setInterval(changeImage, 5000);