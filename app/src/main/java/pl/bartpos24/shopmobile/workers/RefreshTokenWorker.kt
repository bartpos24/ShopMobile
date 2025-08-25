package pl.bartpos24.shopmobile.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker
import javax.inject.Inject

class RefreshTokenWorker @Inject constructor(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @SuppressLint("HardwareIds")
    override suspend fun doWork(): Result {
        return Result.success()
    }
}