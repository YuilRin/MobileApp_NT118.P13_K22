# Ứng dụng quản lý tài chính cá nhân và doanh nghiệp

![img](app/src/main/res/drawable/logo.png)

Ứng dụng được xây dựng nhằm mục đích giúp quản lý chi tiêu hợp lý cho cá nhân và doanh nghiệp trở nên hiệu quả hơn, đây là đồ án cho môn học phát triển ứng dụng trên thiết bị di động tại UIT. Respository này là cho mobiledev được đóng góp bởi 4 thành viên.

## Table of Contents
  - [Ứng dụng quản lý tài chính cá nhân và doanh nghiệp](#Ứng-dụng-quản-lý-tài-chính-cá-nhân-và-doanh_nghiệp)
      -  [Table of Contents](#table-of-contents)
      -  [Quy tắc làm việc chung trên github](#quy-tắc-làm-việc-chung-trên-github)
      -  [Tổng quan về đồ án](#tổng-quan-về-đồ-án)
      -  [Các tính năng chính của ứng dụng](#các-tính-năng-chính-của-ứng-dụng)
      -  [kiến trúc hệ thống và công nghệ sử dụng](#kiến-trúc-hệ-thống-và-công-nghệ-sử-dụng)
   
  ## Quy tắc làm việc chung trên github
  - Hãy clone nhánh `main` của repository này về để có thể làm việc trên local. Sau này nếu có sự thay đổi mới trên github, hãy sử dụng `git pull` để kéo về local sau đó tiếp tục làm việc nha.😊
  - Khi có sự thay đổi của mình thì hãy `commit` ghi rõ nội dung và quy tắc là sau khi hoàn thành một tính năng nào cụ thể nào đó, (**đừng commit dồn** 😞)
  - Mỗi khi `push` lên github **tuyệt đối** không push lênh nhánh chính `main` mà hãy `push` lên nhánh `dev`, chỉ `merge` vào nhánh chính sau khi đã kiểm tra kỹ lưỡng và thông qua code review.
  - Luôn luôn `git pull` hoặc `git fetch` từ nhánh chính trước khi `push` để đảm bảo rằng, chúng ta đang làm việc với phiên bản mới nhất của mã nguồn. Nếu có xung đột giữa mã của bạn và mã repository thì hãy giải quyết các xung  đột này trước khi tiếp tục `push`.
  - Khi muốn thay đổi của mình được merge vào `main`, tạo pull request và nhờ người khác trong team vào review request.
  - Xây dựng một tính năng nào quan trọng thì tạo nhánh riêng từ `dev` với quy tắc đặt tên là `<tên feature đó>-feature`. Khi hoàn thành tính năng đó thì mọi người `merge` vào nhánh `dev` rồi xóa nhánh `feature` đó đi hoặc giữ lại nếu còn tồn tại bug nào đó.
  - Khi có bug nặng, khó fix thì tạo thêm nhánh `fix-bug-x-in-y` từ nhánh mình **đang làm** với `x` là tên bug và `y` là tên nhánh. Sau khi đã kiểm chứng không còn xuất hiện bug, tiến hành merge vào nhánh **gốc** và xóa nhánh hiện tại đi.
  - Quy tắc chúng ta cũng sẽ bàn sau nữa trước mắt là như này ha 🔥
