<?php

namespace App\Traits;

use App\Models\Log;
use Illuminate\Support\Facades\Auth;

trait HasLog
{
    public static function bootHasLog()
    {
        static::created(function ($model) {
            $model->recordLog('create');
        });

        static::updated(function ($model) {
            if ($model->wasChanged('log_id') && count($model->getChanges()) === 1) {
                return;
            }
            $model->recordLog('update');
        });

        static::deleted(function ($model) {
            $model->recordLog('delete');
        });
    }

    protected function recordLog($action)
    {
        $user = Auth::user();

        $companyId = null;
        if (isset($this->company_id)) {
            $companyId = $this->company_id;
        }

        $log = Log::create([
            'action' => $action,
            'model' => static::class,
            'model_id' => $this->id,
            'user_id' => $user ? $user->id : null,
            'company_id' => $companyId,
            'data' => $this->toArray(),
        ]);

        if ($action !== 'delete' || method_exists($this, 'trashed')) {
            // Only set log_id if the model has that column
            if (in_array('log_id', $this->getFillable()) || $this->hasAttribute('log_id') || \Schema::hasColumn($this->getTable(), 'log_id')) {
                $this->log_id = $log->id;
                $this->saveQuietly();
            }
        }
    }
}
