module.exports = {
    success: (status, message, data) => ({
      status,
      success: true,
      message,
      data,
    }),
    fail: (status, message,data) => ({
      status,
      success: false,
      message,
      data,
    }),
  };