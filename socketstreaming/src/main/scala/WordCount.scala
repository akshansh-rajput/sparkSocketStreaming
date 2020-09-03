import org.apache.spark._
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

object WordCount {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("wordCount")
    val ssc = new StreamingContext(conf, Seconds(3))
    ssc.sparkContext.setLogLevel("OFF")
    val lines = ssc.socketTextStream("localhost", 5555)
    lines.foreachRDD { x =>
      if (!x.isEmpty()) {
        try {
          val words = x.flatMap(_.split(" "))
          val keyPair = words.map(w => (w, 1))
          val count = keyPair.reduceByKey(_ + _)
          count.collect.foreach(println)
        } catch {
          case e: Exception =>
            println("ERROR")
            e.printStackTrace()
        }
      }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}