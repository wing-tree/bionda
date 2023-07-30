package wing.tree.bionda.data.source.local

import wing.tree.bionda.data.database.dao.ForecastDao
import wing.tree.bionda.data.model.forecast.local.Forecast

class ForecastDataSource(private val forecastDao: ForecastDao) {
    suspend fun insert(forecast: Forecast) {
        forecastDao.insert(forecast)
    }

    suspend fun load(requestDate: String, requestTime: String): Forecast? {
        return forecastDao.load(requestDate, requestTime)
    }

    suspend fun clear() {
        forecastDao.clear()
    }
}
