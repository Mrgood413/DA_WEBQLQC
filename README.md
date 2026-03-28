 
2280602948_Nguyễn Quang Thành
2280600602_Nguyễn Lê Thành Đạt
2280602065_Đoàn Minh Nghĩa
2280602065_Nguyễn Nhật Minh

-Một cửa hàng quán cà phê cần một website quản lý quán cafe như sau:phải có phân quyền người dùng nhân viên quản lý số lượng món và tiến độ món đang làm,quản trị viên quản lý nhân viên vai trò và doanh thu,khách hàng nhập số bàn,chọn món ăn thêm vào giỏ hàng và sau đó xác nhận món,hiện tại chỉ đặt ở của hàng

Giao diện FrontEnd
-khi mới đăng nhập đường link:
	+hiện trang login chức năng đăng nhập nhân viên,ở dưới nếu không phải là nhân viên thì chọn đăng nhập là khách hàng.
-sau khi đăng nhập là khách hàng:
	+trước khi vào trang phải nhập số bàn
	+hiện menu các món chức năng dưới dạng grid và tên món ăn đó,hình ảnh trong card có nút stepper và nút ở cạnh gọi là thêm giỏ hàng
	+có 2 tab một cái là menu gọi món,một cái là quản lý giỏ hàng
	+trong tab quản lý có các đơn gọi món nhấn vào hiện tất cả món trong giỏ the dạng grid
	+các đơn gọi món hiện theo kiểu Step Progress Bar có ngày xác nhận giỏ hàng ở cạnh có button xác nhận giỏ hàng ,khi thêm giỏ hàng thì tạo đơn hàng,cùng lúc chỉ có thể sử dụng giỏ hàng đó cho đến khi khách hàng bấm xác nhận, sau khi bấm xác nhận thì có thể hủy đơn hàng đó nếu đơn đó đã xác nhận bởi nhân viên đang thực hiện thì không thể hủy được nữa
-sau khi đăng nhập là nhân viên:
	+có tab quản lý món cho nhân viên có thể cập nhật lại số lượng món,ẩn món,có thể thêm và viết phần chi tiết của món
	+có tab hàng đợi giỏ hàng khác hàng có button xác nhận có hiện số bàn,sau khi khác ăn xong thì nhân viên có thể xác nhận thanh toán và chọn phương thức thanh toán của khách hàng
-sau khi đăng nhập là quản trị viên:
	+có một tab là quản lý danh mục,thêm danh mục
	+có một tab dùng để quản lý nhân viên từ tên tuổi ca làm nam/nữ quản trị có thể thêm hoặc sửa/xóa
	+có một tab dùng để quản lý doanh thu của quán có chức năng xem doanh thu từng món có thể để theo kiểu từng ngày/tuần/tháng/năm,có cả tổng doanh thu của cửa hàng sort cũng giống như món ăn,có cả biểu đồ và có thể xuất ra file excel
-trường hợp vào trang không có quyền thì hiện trang lỗi bạn không có quyền
BackEnd 
-sử dụng validate cho kiểm tra input
-phân quyền cho staff,admin
-sử dụng mysql lưu trữ sài jpa
-sài cookie nhớ phiên đăng nhập
-có nút logout xóa cookie

-làm thêm trang đăng ký,ship hàng cho người đã đăng ký