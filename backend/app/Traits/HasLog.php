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
        
        $accountId = null;
        if (isset($this->account_id)) {
            $accountId = $this->account_id;
        }

        $log = Log::create([
            'action' => $action,
            'model' => static::class,
            'model_id' => $this->id,
            'user_id' => $user ? $user->id : null,
            'account_id' => $accountId,
            'data' => $this->toArray(),
        ]);

        if ($action !== 'delete' || method_exists($this, 'trashed')) {
             $this->log_id = $log->id;
             $this->saveQuietly();
        }
    }
}
