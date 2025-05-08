class ApiRespose:
    def __init__(self, error:str = None, message:str = None, data = None):
        self.error = error
        self.message = message
        self.data = data

    def to_dict(self):
        return {
            "error": self.error,
            "message": self.message,
            "data": self.data,
        }
