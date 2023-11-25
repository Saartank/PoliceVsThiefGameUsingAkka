package api_client

case class StatusResponse(message: String,
                          data: (((String, Int), Map[Int, (Int, Int)]),
                            ((String, Int), Map[Int, (Double, Int, Int)])))

