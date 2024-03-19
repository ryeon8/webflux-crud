from locust import HttpUser, TaskSet, task, between

class UserBehavior(TaskSet):
    @task(1)
    def test_noti_endpoint(self):
        # 부하 테스트할 엔드포인트에 대한 요청을 보냅니다.
        self.client.get("/noti/list?page=1&size=10")

class WebsiteUser(HttpUser):
    # 위에서 정의한 사용자 동작을 사용합니다.
    tasks = [UserBehavior]
    # 사용자가 요청 사이의 대기 시간을 지정합니다.
    wait_time = between(5, 9)  # 요청 사이의 대기 시간을 5~9초 사이로 설정합니다.
